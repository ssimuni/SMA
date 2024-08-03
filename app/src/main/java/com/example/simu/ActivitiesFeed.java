package com.example.simu;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ActivitiesFeed extends AppCompatActivity {

    private PostsAdapter postsAdapter;
    private RecyclerView attendanceRecyclerView;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_feed);
        fAuth = FirebaseAuth.getInstance();

        attendanceRecyclerView = findViewById(R.id.attendanceRecyclerView);
        attendanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        postsAdapter = new PostsAdapter(this);
        attendanceRecyclerView.setAdapter(postsAdapter);

        loadPosts();

    }


    private void loadPosts(){
        FirebaseFirestore.getInstance()
                .collection("Attendance")
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
                        Toast.makeText(ActivitiesFeed.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}