package com.example.simu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class NewsFeed extends AppCompatActivity {

    private TextView mUp_post;
    private PostsAdapter postsAdapter;
    private RecyclerView postRecyclerView;
    private ImageView newsfeedDp;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        fAuth = FirebaseAuth.getInstance();

        postRecyclerView = findViewById(R.id.postRecyclerView);
        mUp_post = findViewById(R.id.up_post);
        newsfeedDp = findViewById(R.id.newsfeedDp);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        postsAdapter = new PostsAdapter(this);
        postRecyclerView.setAdapter(postsAdapter);
        loadPosts();
        loadUserProfileImage();



        mUp_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(NewsFeed.this, Upload_post.class));}
        });
    }



    private void loadUserProfileImage() {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String userProfileImage = documentSnapshot.getString("profileImageUrl");
                        if (userProfileImage != null) {
                            Glide.with(NewsFeed.this).load(userProfileImage).into(newsfeedDp);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewsFeed.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadPosts(){
        FirebaseFirestore.getInstance()
                .collection("Posts")
                .orderBy("postingTime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        postsAdapter.clearPost();
                        List<DocumentSnapshot> dslist = queryDocumentSnapshots.getDocuments();

                        for(DocumentSnapshot ds:dslist){
                            PostModel postModel = ds.toObject(PostModel.class);
                            postsAdapter.addPost(postModel);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewsFeed.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}