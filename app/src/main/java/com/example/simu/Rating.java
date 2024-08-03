package com.example.simu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.EventListener;

import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;


public class Rating extends AppCompatActivity {

    RatingBar mRating;
    Button mSubmit;
    TextView mThank, avgRate, numOfPeopleRatedUs;
    EditText feedback;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;
    private DocumentReference ratingRef, userRatingRef;
    private String currentUserId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        mRating = findViewById(R.id.rating);
        mSubmit = findViewById(R.id.submit);
        mThank = findViewById(R.id.thank);
        avgRate = findViewById(R.id.avgRate);
        numOfPeopleRatedUs = findViewById(R.id.numOfPeopleRatedUs);
        feedback = findViewById(R.id.feedback);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ratingRef = db.collection("ratings").document("appRating");
        userRatingRef = db.collection("userRatings").document(currentUserId);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        updateUIWithRatingStats();
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = mRating.getRating();
                String userId = mAuth.getCurrentUser().getUid();
                String feedbackText = feedback.getText().toString();

                db.collection("ratings")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {
                                        // User has not rated before, increment numOfPeopleRatedUs
                                        incrementNumOfPeopleRatedUs();
                                    }
                                    // Add or update the rating
                                    addOrUpdateRating(userId, rating, feedbackText);
                                }
                            }
                        });

                db.collection("ratings")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    return;
                                }
                                updateUIWithRatingStats();
                            }
                        });
                    mThank.setVisibility(View.VISIBLE);
                    mSubmit.setVisibility(View.INVISIBLE);
                    feedback.setText("");

                    if (rating == 5) {
                        mThank.setText(R.string.thank_you);
                    } else if (rating == 0) {
                        mThank.setText(R.string.very_disappointing);
                    } else {
                        mThank.setText(R.string.thank_you_for_your_feedback);
                    }
                }
        });
    }

    private void incrementNumOfPeopleRatedUs() {

        DocumentReference docRef = db.collection("rating_stats").document("app_stats");
        docRef.update("numOfPeopleRatedUs", FieldValue.increment(1));
    }


    private void addOrUpdateRating(String userId, float rating, String feedbackText) {
        // Add or update the rating
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("userId", userId);
        ratingData.put("rating", rating);
        ratingData.put("feedbackText", feedbackText);

        db.collection("ratings").document(userId)
                .set(ratingData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }

    private void updateUIWithRatingStats() {
        db.collection("ratings")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int numOfRatings = queryDocumentSnapshots.size();
                        float totalRating = 0;

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            totalRating += document.getDouble("rating");
                        }

                        float avgRating = totalRating / numOfRatings;

                        avgRate.setText("Average Rate: " + avgRating);
                        numOfPeopleRatedUs.setText("Number of people who have rated us: " + numOfRatings);
                    }
                });
    }
}