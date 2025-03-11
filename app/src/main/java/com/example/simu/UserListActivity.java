package com.example.simu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UserListActivity extends AppCompatActivity {

    private ListView listViewUsers;
    private FirebaseFirestore db;
    private List<MonthlyReport.User> userList;
    private List<MonthlyReport.User> filteredUserList;
    private List<String> userNames;
    private List<String> workstations;
    private List<String> designations;
    private List<String> divisions;
    private List<String> districts;
    private List<String> upozilas;
    Button toggleSpinnersBtn;
    LinearLayout spinnerLayout;
    private Spinner spinnerWorkstation;
    private Spinner spinnerDesignation;
    private Spinner spinnerDivision;
    private Spinner spinnerDistrict;
    private Spinner spinnerUpozila;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        spinnerLayout = findViewById(R.id.spinnerLayout);
        toggleSpinnersBtn = findViewById(R.id.toggleSpinnersBtn);

        listViewUsers = findViewById(R.id.listViewUsers);
        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        filteredUserList = new ArrayList<>();
        userNames = new ArrayList<>();
        workstations = new ArrayList<>();
        designations = new ArrayList<>();
        divisions = new ArrayList<>();
        districts = new ArrayList<>();
        upozilas = new ArrayList<>();

        spinnerWorkstation = findViewById(R.id.spinnerWorkstation);
        spinnerDesignation = findViewById(R.id.spinnerDesignation);
        spinnerDivision = findViewById(R.id.spinnerDivision);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerUpozila = findViewById(R.id.spinnerUpozila);

        loadUsers();
        loadUniqueSpinnerValues();
        setSpinnerListeners();

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

        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MonthlyReport.User selectedUser = filteredUserList.get(position);
                Intent intent = new Intent(UserListActivity.this, UserPostsActivity.class);
                intent.putExtra("userId", selectedUser.getUserId());
                startActivity(intent);
            }
        });
    }

    private void loadUniqueSpinnerValues() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            HashMap<String, Integer> divisionCounts = new HashMap<>();
                            HashMap<String, Integer> districtCounts = new HashMap<>();
                            HashMap<String, Integer> upazilaCounts = new HashMap<>();

                            for (DocumentSnapshot document : task.getResult()) {
                                String workstation = document.getString("workstation");
                                String designation = document.getString("designation");
                                String division = document.getString("division");
                                String district = document.getString("district");
                                String upozila = document.getString("upozila");

                                if (!workstations.contains(workstation)) workstations.add(workstation);
                                if (designation != null && !designation.isEmpty() && !designations.contains(designation))
                                    designations.add(designation);
                                if (!divisions.contains(division)) divisions.add(division);
                                if (!districts.contains(district)) districts.add(district);
                                if (!upozilas.contains(upozila)) upozilas.add(upozila);

                                if (division != null) divisionCounts.put(division, divisionCounts.getOrDefault(division, 0) + 1);
                                if (district != null) districtCounts.put(district, districtCounts.getOrDefault(district, 0) + 1);
                                if (upozila != null) upazilaCounts.put(upozila, upazilaCounts.getOrDefault(upozila, 0) + 1);
                            }
                            setupSpinners(divisionCounts, districtCounts, upazilaCounts);
                        } else {
                            Toast.makeText(UserListActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void setupSpinners(HashMap<String, Integer> divisionCounts, HashMap<String, Integer> districtCounts, HashMap<String, Integer> upazilaCounts) {
        // Add "Select" default values
        workstations.add(0, "Select Workstation");
        designations.add(0, "Select Designation");
        divisions.add(0, "Select Division");
        districts.add(0, "Select District");
        upozilas.add(0, "Select Upazila");

        // Append counts to each location
        for (int i = 1; i < divisions.size(); i++) {
            String key = divisions.get(i);
            divisions.set(i, key + " (" + divisionCounts.getOrDefault(key, 0) + ")");
        }
        for (int i = 1; i < districts.size(); i++) {
            String key = districts.get(i);
            districts.set(i, key + " (" + districtCounts.getOrDefault(key, 0) + ")");
        }
        for (int i = 1; i < upozilas.size(); i++) {
            String key = upozilas.get(i);
            upozilas.set(i, key + " (" + upazilaCounts.getOrDefault(key, 0) + ")");
        }

        // Create Adapters
        ArrayAdapter<String> workstationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, workstations);
        ArrayAdapter<String> designationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, designations);
        ArrayAdapter<String> divisionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, divisions);
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districts);
        ArrayAdapter<String> upozilaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, upozilas);

        // Set DropDown View Resource
        workstationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        designationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        upozilaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attach Adapters to Spinners
        spinnerWorkstation.setAdapter(workstationAdapter);
        spinnerDesignation.setAdapter(designationAdapter);
        spinnerDivision.setAdapter(divisionAdapter);
        spinnerDistrict.setAdapter(districtAdapter);
        spinnerUpozila.setAdapter(upozilaAdapter);
    }


    private void setSpinnerListeners() {
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterUsers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        };

        spinnerWorkstation.setOnItemSelectedListener(spinnerListener);
        spinnerDesignation.setOnItemSelectedListener(spinnerListener);
        spinnerDivision.setOnItemSelectedListener(spinnerListener);
        spinnerDistrict.setOnItemSelectedListener(spinnerListener);
        spinnerUpozila.setOnItemSelectedListener(spinnerListener);
    }

    private void filterUsers() {
        filteredUserList.clear();

        String selectedWorkstation = spinnerWorkstation.getSelectedItem().toString();
        String selectedDesignation = spinnerDesignation.getSelectedItem().toString();
        String selectedDivision = spinnerDivision.getSelectedItem().toString();
        String selectedDistrict = spinnerDistrict.getSelectedItem().toString();
        String selectedUpozila = spinnerUpozila.getSelectedItem().toString();

        for (MonthlyReport.User user : userList) {
            if ((selectedWorkstation.equals("Select Workstation") || user.getWorkstation().equals(selectedWorkstation)) &&
                    (selectedDesignation.equals("Select Designation") || user.getDesignation().equals(selectedDesignation)) &&
                    (selectedDivision.equals("Select Division") || user.getDivision().equals(selectedDivision)) &&
                    (selectedDistrict.equals("Select District") || user.getDistrict().equals(selectedDistrict)) &&
                    (selectedUpozila.equals("Select Upazila") || user.getUpozila().equals(selectedUpozila))) {
                filteredUserList.add(user);
            }
        }

        userNames.clear();
        for (MonthlyReport.User user : filteredUserList) {
            userNames.add(user.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(UserListActivity.this, android.R.layout.simple_list_item_1, userNames);
        listViewUsers.setAdapter(adapter);
    }

    private void loadUsers() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String userId = document.getId();
                                String name = document.getString("name");
                                String designation = document.getString("designation");
                                String workstation = document.getString("workstation");
                                String division = document.getString("division");
                                String district = document.getString("district");
                                String upozila = document.getString("upozila");

                                designation = designation != null ? designation : "Unknown Designation";

                                MonthlyReport.User user = new MonthlyReport.User(userId, name, designation, workstation, division, district, upozila);
                                userList.add(user);
                                userNames.add(user.getName());
                            }

                            filteredUserList.addAll(userList);
                            userNames.clear();
                            for (MonthlyReport.User user : filteredUserList) {
                                userNames.add(user.getName());
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(UserListActivity.this, android.R.layout.simple_list_item_1, userNames);
                            listViewUsers.setAdapter(adapter);
                        } else {
                            Toast.makeText(UserListActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
