package com.example.simu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simu.databinding.ActivityCommentsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

public class CommentsActivity extends AppCompatActivity {
    ActivityCommentsBinding binding;
    private String postTd;
    private CommentsAdapter commentsAdapter;
    private FirebaseFirestore firestore;

    private int currentPage = 0;
    private int totalPages = 0;


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

        loadComments();

        binding.sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = binding.commentEd.getText().toString();
                if(comment.trim().length()>0){
                    comment(comment);
                }
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
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(CommentsActivity.this, "Failed to load ratings and comments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Calculate the total number of pages based on the number of ratings and comments
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        int totalItems = documents.size();
                        totalPages = (totalItems + 4) / 5; // Round up to the nearest whole number

                        commentsAdapter.clearPost();

                        // Display ratings and comments for the current page
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


//    private void loadComments(){
//        FirebaseFirestore
//                .getInstance()
//                .collection("Comments")
//                .whereEqualTo("postId", postTd)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        commentsAdapter.clearPost();
//                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
//                        for(DocumentSnapshot ds:dsList){
//                            CommentModel commentModel = ds.toObject(CommentModel.class);
//                            commentsAdapter.addPost(commentModel);
//                        }
//                    }
//                });
//    }




    private void comment(String comment) {
        String id = UUID.randomUUID().toString();
        CommentModel commentModel = new CommentModel(id, postTd, FirebaseAuth.getInstance().getUid(), comment);
        FirebaseFirestore.getInstance()
                .collection("Comments")
                .document(id)
                .set(commentModel);
        commentsAdapter.addPost(commentModel);
    }
}