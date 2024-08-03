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

                            break;
                    }
                    return classificationResults;
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {

        } finally {
            executor.shutdown();
        }
        return results;
    }

    private void initClassifier(Context context) {
        BaseOptions.Builder baseOptionsBuilder = BaseOptions.builder();

        switch (_currentDelegate) {
            case DELEGATE_CPU:
                // Default
                break;
            case DELEGATE_NNAPI:
                baseOptionsBuilder.useNnapi();
                break;
        }
        BaseOptions baseOptions = baseOptionsBuilder.build();

        if (_currentModel.equals(MOBILEBERT)) {
            try {
                BertNLClassifier.BertNLClassifierOptions options = BertNLClassifier.BertNLClassifierOptions.builder()
                        .setBaseOptions(baseOptions)
                        .build();
                bertClassifier = BertNLClassifier.createFromFileAndOptions(context, MOBILEBERT, options);
            } catch (IOException e) {
                e.printStackTrace();

            }
        } else if (_currentModel.equals(WORD_VEC)) {
            try {
                NLClassifier.NLClassifierOptions options = NLClassifier.NLClassifierOptions.builder()
                        .setBaseOptions(baseOptions)
                        .build();
                nlClassifier = NLClassifier.createFromFileAndOptions(context, WORD_VEC, options);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    public static final int DELEGATE_CPU = 0;
    public static final int DELEGATE_NNAPI = 1;
    public static final String WORD_VEC = "wordvec.tflite";
    public static final String MOBILEBERT = "mobilebert.tflite";
}
