package com.example.simu;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.ViewModel;

import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.text.nlclassifier.BertNLClassifier;
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@SuppressLint("StaticFieldLeak")
public class TextClassificationViewModel extends ViewModel {
    private int _currentDelegate = DELEGATE_CPU;
    private String _currentModel = MOBILEBERT;

    // classifiers
    private BertNLClassifier bertClassifier;
    private NLClassifier nlClassifier;

    private ScheduledExecutorService executor;

    public TextClassificationViewModel(Context context) {
        initClassifier(context);
    }

    public List<Float> classify(String text) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        List<Float> results = new ArrayList<>();
        try {
            results = executor.submit(new Callable<List<Float>>() {
                @Override
                public List<Float> call() throws Exception {
                    List<Float> classificationResults = new ArrayList<>();
                    switch (_currentModel) {
                        case MOBILEBERT:
                            for (Category category : bertClassifier.classify(text)) {
                                classificationResults.add(category.getScore());
                            }
                            break;
                        case WORD_VEC:
                            for (Category category : nlClassifier.classify(text)) {
                                classificationResults.add(category.getScore());
                            }
                            break;
                        default:
                            // Empty block for handling unknown models
                            break;
                    }
                    return classificationResults;
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            // Handle potential exceptions during classification
            // Return an empty list or throw an exception if needed
        } finally {
            executor.shutdown(); // Shutdown the executor after use
        }
        return results;
    }

    private void initClassifier(Context context) {
        BaseOptions.Builder baseOptionsBuilder = BaseOptions.builder();
        // enable hardware acceleration(optional) when _currentDelegate is on
        switch (_currentDelegate) {
            case DELEGATE_CPU:
                // Default
                break;
            case DELEGATE_NNAPI:
                baseOptionsBuilder.useNnapi();
                break;
        }
        BaseOptions baseOptions = baseOptionsBuilder.build();

        // Create the settings for the model by building a classifier object
        if (_currentModel.equals(MOBILEBERT)) {
            try {
                BertNLClassifier.BertNLClassifierOptions options = BertNLClassifier.BertNLClassifierOptions.builder()
                        .setBaseOptions(baseOptions)
                        .build();
                bertClassifier = BertNLClassifier.createFromFileAndOptions(context, MOBILEBERT, options);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle IOException (e.g., log the error, show a message to the user, etc.)
            }
        } else if (_currentModel.equals(WORD_VEC)) {
            try {
                NLClassifier.NLClassifierOptions options = NLClassifier.NLClassifierOptions.builder()
                        .setBaseOptions(baseOptions)
                        .build();
                nlClassifier = NLClassifier.createFromFileAndOptions(context, WORD_VEC, options);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle IOException (e.g., log the error, show a message to the user, etc.)
            }
        }
    }

    public static final int DELEGATE_CPU = 0;
    public static final int DELEGATE_NNAPI = 1;
    public static final String WORD_VEC = "wordvec.tflite";
    public static final String MOBILEBERT = "mobilebert.tflite";
}
