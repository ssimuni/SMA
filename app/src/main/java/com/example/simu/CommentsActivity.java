package com.example.simu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.simu.databinding.ActivityCommentsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;

public class CommentsActivity extends AppCompatActivity {
    ActivityCommentsBinding binding;
    private String postTd;
    private CommentsAdapter commentsAdapter;
    private FirebaseFirestore firestore;
    private EditText commentEd;

    private int currentPage = 0;
    private int totalPages = 0;

    private TextClassificationViewModel textClassificationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        postTd = getIntent().getStringExtra("id");
        commentsAdapter = new CommentsAdapter(this);
        binding.recycler.setAdapter(commentsAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        firestore = FirebaseFirestore.getInstance();
        commentEd = findViewById(R.id.commentEd);

        textClassificationViewModel = new TextClassificationViewModel(this);
        loadComments();

        binding.sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = binding.commentEd.getText().toString().trim();
                if (!comment.isEmpty()) {
                    classifyAndSaveComment(comment);
                }
                commentEd.setText("");
            }
        });

        binding.prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage > 0) {
                    currentPage--;
                    loadComments();
                }
            }
        });
        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage < totalPages - 1) {
                    currentPage++;
                    loadComments();
                }
            }
        });
    }

    private void loadComments() {
        firestore.collection("Comments")
                .orderBy("commentTime", Query.Direction.DESCENDING)
                .whereEqualTo("postId", postTd)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@NonNull QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(CommentsActivity.this, "Failed to load comments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        int totalItems = documents.size();
                        totalPages = (totalItems + 4) / 5;

                        commentsAdapter.clearPost();

                        int startIndex = currentPage * 5;
                        int endIndex = Math.min(startIndex + 5, totalItems);
                        for (int i = startIndex; i < endIndex; i++) {
                            DocumentSnapshot documentSnapshot = documents.get(i);
                            CommentModel commentModel = documentSnapshot.toObject(CommentModel.class);
                            commentsAdapter.addPost(commentModel);
                        }
                        updatePageNumbers();
                    }
                });
    }

    private void classifyAndSaveComment(String comment) {
        List<Float> results = textClassificationViewModel.classify(comment);
        float negativeScore = results.isEmpty() ? 0 : results.get(0);
        float positiveScore = results.size() > 1 ? results.get(1) : 0;

        String sentiment = positiveScore > negativeScore ? "positive" : "negative";

        String id = UUID.randomUUID().toString();
        CommentModel commentModel = new CommentModel(id, postTd, FirebaseAuth.getInstance().getUid(), comment, System.currentTimeMillis(), sentiment, positiveScore, negativeScore);
        FirebaseFirestore.getInstance()
                .collection("Comments")
                .document(id)
                .set(commentModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    private void updatePageNumbers() {
        LinearLayout pageLayout = findViewById(R.id.pageLayout);
        pageLayout.removeAllViews();

        for (int i = 0; i < totalPages; i++) {
            Button button = new Button(this);
            button.setText(String.valueOf(i + 1));
            button.setTag(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPage = (int) v.getTag();
                    loadComments();
                }
            });

            if (i == currentPage) {
                button.setEnabled(false);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(8, 0, 8, 0);
            button.setLayoutParams(layoutParams);
            pageLayout.addView(button);
        }
    }
}
