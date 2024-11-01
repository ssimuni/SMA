package com.example.simu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NewsFeed extends AppCompatActivity {

    private TextView mUp_post;
    private PostsAdapter postsAdapter;
    private RecyclerView postRecyclerView;
    private ImageView newsfeedDp;
    private FirebaseAuth fAuth;
    Button toggleSpinnersBtn;
    LinearLayout spinnerLayout;
    private Spinner spinnerWorkstation;
    private Spinner spinnerDesignation;
    private Spinner spinnerDivision;
    private Spinner spinnerDistrict;
    private Spinner spinnerUpozila;
    private List<String> workstationList;
    private List<String> designationList;
    private List<String> divisionList;
    private List<String> districtList;
    private List<String> upozilaList;
    private FirebaseFirestore db;

    private Map<String, User> userMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        spinnerLayout = findViewById(R.id.spinnerLayout);
        toggleSpinnersBtn = findViewById(R.id.toggleSpinnersBtn);

        postRecyclerView = findViewById(R.id.postRecyclerView);
        mUp_post = findViewById(R.id.up_post);
        newsfeedDp = findViewById(R.id.newsfeedDp);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        postsAdapter = new PostsAdapter(this);
        postRecyclerView.setAdapter(postsAdapter);

        spinnerWorkstation = findViewById(R.id.spinnerWorkstation);
        spinnerDesignation = findViewById(R.id.spinnerDesignation);
        spinnerDivision = findViewById(R.id.spinnerDivision);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerUpozila = findViewById(R.id.spinnerUpozila);

        workstationList = new ArrayList<>();
        designationList = new ArrayList<>();
        divisionList = new ArrayList<>();
        districtList = new ArrayList<>();
        upozilaList = new ArrayList<>();

        userMap = new HashMap<>();

        loadUsersFromFirestore();
        loadPosts();
        loadUserProfileImage();

        toggleSpinnersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerLayout.getVisibility() == View.GONE) {
                    spinnerLayout.setVisibility(View.VISIBLE);
                    toggleSpinnersBtn.setText("Hide");
                } else {
                    spinnerLayout.setVisibility(View.GONE);
                    toggleSpinnersBtn.setText("Search");
                }
            }
        });

        mUp_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewsFeed.this, Upload_post.class));
            }
        });

        setSpinnerListeners();
    }

    private void loadUsersFromFirestore() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String userId = document.getId();
                                String name = document.getString("name");
                                String designation = document.getString("designation");
                                String workstation = document.getString("workstation");
                                String division = document.getString("division");
                                String district = document.getString("district");
                                String upozila = document.getString("upozila");

                                designation = (designation != null) ? designation : "";

                                User user = new User(userId, name, designation, workstation, division, district, upozila);
                                userMap.put(userId, user);

                                if (!workstationList.contains(workstation)) {
                                    workstationList.add(workstation);
                                }
                                if (!designationList.contains(designation) && !designation.isEmpty()) {
                                    designationList.add(designation);
                                }
                                if (!divisionList.contains(division)) {
                                    divisionList.add(division);
                                }
                                if (!districtList.contains(district)) {
                                    districtList.add(district);
                                }
                                if (!upozilaList.contains(upozila)) {
                                    upozilaList.add(upozila);
                                }
                            }
                            setSpinnerAdapters();
                        } else {
                        }
                    }
                });
    }

    private void setSpinnerAdapters() {
        workstationList.add(0, "Workstation");
        designationList.add(0, "Designation");
        divisionList.add(0, "Division");
        districtList.add(0, "District");
        upozilaList.add(0, "Upazila");

        ArrayAdapter<String> workstationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, workstationList);
        workstationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkstation.setAdapter(workstationAdapter);

        ArrayAdapter<String> designationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, designationList);
        designationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDesignation.setAdapter(designationAdapter);

        ArrayAdapter<String> divisionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, divisionList);
        divisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDivision.setAdapter(divisionAdapter);

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districtList);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(districtAdapter);

        ArrayAdapter<String> upozilaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, upozilaList);
        upozilaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUpozila.setAdapter(upozilaAdapter);
    }

    private void setSpinnerListeners() {
        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterPosts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        spinnerWorkstation.setOnItemSelectedListener(filterListener);
        spinnerDesignation.setOnItemSelectedListener(filterListener);
        spinnerDivision.setOnItemSelectedListener(filterListener);
        spinnerDistrict.setOnItemSelectedListener(filterListener);
        spinnerUpozila.setOnItemSelectedListener(filterListener);
    }

    private void filterPosts() {
        String selectedWorkstation = spinnerWorkstation.getSelectedItem() != null ? spinnerWorkstation.getSelectedItem().toString() : "";
        String selectedDesignation = spinnerDesignation.getSelectedItem() != null ? spinnerDesignation.getSelectedItem().toString() : "";
        String selectedDivision = spinnerDivision.getSelectedItem() != null ? spinnerDivision.getSelectedItem().toString() : "";
        String selectedDistrict = spinnerDistrict.getSelectedItem() != null ? spinnerDistrict.getSelectedItem().toString() : "";
        String selectedUpozila = spinnerUpozila.getSelectedItem() != null ? spinnerUpozila.getSelectedItem().toString() : "";

        FirebaseFirestore.getInstance()
                .collection("Posts")
                .orderBy("postingTime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        postsAdapter.clearPost();
                        List<DocumentSnapshot> dslist = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot ds : dslist) {
                            PostModel postModel = ds.toObject(PostModel.class);

                            String userId = postModel.getUserId();
                            User user = userMap.get(userId);

                            if (user != null) {
                                boolean matches = true;

                                if (!selectedWorkstation.equals("Workstation") && !user.getWorkstation().equals(selectedWorkstation)) {
                                    matches = false;
                                }

                                if (!selectedDesignation.equals("Designation") && !user.getDesignation().equals(selectedDesignation)) {
                                    matches = false;
                                }

                                if (!selectedDivision.equals("Division") && !user.getDivision().equals(selectedDivision)) {
                                    matches = false;
                                }

                                if (!selectedDistrict.equals("District") && !user.getDistrict().equals(selectedDistrict)) {
                                    matches = false;
                                }

                                if (!selectedUpozila.equals("Upazila") && !user.getUpozila().equals(selectedUpozila)) {
                                    matches = false;
                                }

                                if (matches) {
                                    postsAdapter.addPost(postModel);
                                }
                            }
                        }
                    }
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

                        for (DocumentSnapshot ds : dslist) {
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

    static class User {
        private String userId;
        private String name;
        private String designation;
        private String workstation;
        private  String division;
        private String district;
        private String upozila;

        public User(String userId, String name, String designation, String workstation, String division, String district, String upozila) {
            this.userId = userId;
            this.name = name;
            this.designation = designation;
            this.workstation = workstation;
            this.division = division;
            this.district = district;
            this.upozila = upozila;
        }

        public String getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }

        public String getDesignation() {
            return designation;
        }

        public String getWorkstation() {
            return workstation;
        }

        public String getDivision() { return division; }

        public String getDistrict() {
            return district;
        }

        public String getUpozila() {
            return upozila;
        }
    }
}