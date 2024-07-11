package com.example.simu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Profile extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView nameTextView, addressTextView, workstationTextView, emailTextView, nidTextView, dobTextView, designationTextView, divisionTextView, districtTextView, upozilaTextView;
    private Button logoutButton;
    FirebaseAuth fAuth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        profileImageView = findViewById(R.id.profileImageView);
        nameTextView = findViewById(R.id.nameTextView);
        addressTextView = findViewById(R.id.addressTextView);
        workstationTextView = findViewById(R.id.workstationTextView);
        emailTextView = findViewById(R.id.emailTextView);
        nidTextView = findViewById(R.id.nidTextView);
        dobTextView = findViewById(R.id.dobTextView);
        designationTextView = findViewById(R.id.designationTextView);
        logoutButton = findViewById(R.id.logoutButton);
        divisionTextView = findViewById(R.id.division);
        districtTextView = findViewById(R.id.district);
        upozilaTextView = findViewById(R.id.upozila);

        fAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
                String userID = user.getUid();
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(userID);

                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // User data found in Firestore
                                String name = document.getString("name");
                                String address = document.getString("address");
                                String workstation = document.getString("workstation");
                                String email = document.getString("email");
                                String nid = document.getString("nid");
                                String dob = document.getString("dob");
                                String designation = document.getString("designation");
                                String profileImageUrl = document.getString("profileImageUrl");
                                String division = document.getString("division");
                                String district = document.getString("district");
                                String upozila = document.getString("upozila");

                                // Display user information
                                nameTextView.setText(String.format("Name: %s", name));
                                addressTextView.setText(String.format("Address: %s", address));
                                workstationTextView.setText(String.format("Work station: %s", workstation));
                                emailTextView.setText(String.format("Email: %s", email));
                                nidTextView.setText(String.format("NID: %s", nid));
                                dobTextView.setText(String.format("Date of Birth: %s", dob));
                                designationTextView.setText(String.format("Designation: %s", designation));
                                divisionTextView.setText(String.format("Division: %s", division));
                                districtTextView.setText(String.format("District: %s", district));
                                upozilaTextView.setText(String.format("Upazila: %s", upozila));

                                loadProfileImage(userID);
                            }
                        }
                    }
                });
            }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearUserSession();
                Intent intent = new Intent(Profile.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void loadProfileImage(String userID) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userID = user.getUid();
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(userID);

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String profileImageUrl = document.getString("profileImageUrl");

                            if (isValidFirebaseStorageUrl(profileImageUrl)) {
                                loadImageFromUrl(profileImageUrl);
                            }
                        }
                    } else {
                        Log.e("Profile", "Error fetching user document: " + task.getException());
                    }
                }
            });
        }
    }

    private boolean isValidFirebaseStorageUrl(String url) {
        if (url != null) {
            Uri uri = Uri.parse(url);
            // Check the schema (https)
            if ("https".equals(uri.getScheme())) {
                // Check the host (Firebase Storage bucket)
                if ("firebasestorage.googleapis.com".equals(uri.getHost())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void loadImageFromUrl(String url) {
        loadImage(url);
    }

    private void loadImage(String url) {
        Glide.with(this)
                .load(url)
                .into(profileImageView);
    }

    private void clearUserSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
    }
}
