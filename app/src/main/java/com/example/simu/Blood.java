package com.example.simu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class Blood extends AppCompatActivity {
    private Button toggleSpinnersBtn;
    private LinearLayout spinnerLayout;
    private UserAdapter userAdapter;
    private Spinner spinnerWorkstation, spinnerDesignation, spinnerDivision, spinnerDistrict, spinnerUpozila, spinnerBlood;
    private List<User> userList = new ArrayList<>();
    private List<String> workstationList = new ArrayList<>();
    private List<String> designationList = new ArrayList<>();
    private List<String> divisionList = new ArrayList<>();
    private List<String> districtList = new ArrayList<>();
    private List<String> upozilaList = new ArrayList<>();
    private List<String> bloodList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood);

        db = FirebaseFirestore.getInstance();
        userAdapter = new UserAdapter(this, userList);
        ((ListView) findViewById(R.id.listViewUsers)).setAdapter(userAdapter);
        toggleSpinnersBtn = findViewById(R.id.toggleSpinnersBtn);
        spinnerLayout = findViewById(R.id.spinnerLayout);

        spinnerWorkstation = findViewById(R.id.spinnerWorkstation);
        spinnerDesignation = findViewById(R.id.spinnerDesignation);
        spinnerDivision = findViewById(R.id.spinnerDivision);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerUpozila = findViewById(R.id.spinnerUpozila);
        spinnerBlood = findViewById(R.id.spinnerBlood);

        loadUserData();
        loadSpinnerData();

        toggleSpinnersBtn.setOnClickListener(v -> {
            spinnerLayout.setVisibility(spinnerLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            toggleSpinnersBtn.setText(spinnerLayout.getVisibility() == View.VISIBLE ? "Hide" : "Search");
        });

        setSpinnerListeners();
    }

    private void setSpinnerListeners() {
        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerWorkstation.setOnItemSelectedListener(filterListener);
        spinnerDesignation.setOnItemSelectedListener(filterListener);
        spinnerDivision.setOnItemSelectedListener(filterListener);
        spinnerDistrict.setOnItemSelectedListener(filterListener);
        spinnerUpozila.setOnItemSelectedListener(filterListener);
        spinnerBlood.setOnItemSelectedListener(filterListener);
    }

    private void filterData() {
        List<User> filteredList = new ArrayList<>();

        String workstationSelection = spinnerWorkstation.getSelectedItem() != null ? spinnerWorkstation.getSelectedItem().toString() : "Select Workstation";
        String designationSelection = spinnerDesignation.getSelectedItem() != null ? spinnerDesignation.getSelectedItem().toString() : "Select Designation";
        String divisionSelection = spinnerDivision.getSelectedItem() != null ? spinnerDivision.getSelectedItem().toString() : "Select Division";
        String districtSelection = spinnerDistrict.getSelectedItem() != null ? spinnerDistrict.getSelectedItem().toString() : "Select District";
        String upozilaSelection = spinnerUpozila.getSelectedItem() != null ? spinnerUpozila.getSelectedItem().toString() : "Select Upozila";
        String bloodSelection = spinnerBlood.getSelectedItem() != null ? spinnerBlood.getSelectedItem().toString() : "Select Blood Group";

        for (User user : userList) {
            boolean matchesFilter = true;

            if (!workstationSelection.equals("Select Workstation") && (user.getWorkstation() == null || !workstationSelection.equals(user.getWorkstation()))) {
                matchesFilter = false;
            }
            if (!designationSelection.equals("Select Designation") && (user.getDesignation() == null || !designationSelection.equals(user.getDesignation()))) {
                matchesFilter = false;
            }
            if (!divisionSelection.equals("Select Division") && (user.getDivision() == null || !divisionSelection.equals(user.getDivision()))) {
                matchesFilter = false;
            }
            if (!districtSelection.equals("Select District") && (user.getDistrict() == null || !districtSelection.equals(user.getDistrict()))) {
                matchesFilter = false;
            }
            if (!upozilaSelection.equals("Select Upozila") && (user.getUpozila() == null || !upozilaSelection.equals(user.getUpozila()))) {
                matchesFilter = false;
            }
            if (!bloodSelection.equals("Select Blood Group") && (user.getBlood() == null || !bloodSelection.equals(user.getBlood()))) {
                matchesFilter = false;
            }

            if (matchesFilter) {
                filteredList.add(user);
            }
        }

        userAdapter.clear();
        userAdapter.addAll(filteredList);
        userAdapter.notifyDataSetChanged();
    }

    private void loadUserData() {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    userList.add(new User(
                            document.getString("name"),
                            document.getString("workstation"),
                            document.getString("designation"),
                            document.getString("district"),
                            document.getString("division"),
                            document.getString("upozila"),
                            document.getString("number"),
                            document.contains("blood") ? document.getString("blood") : "Not Available"
                    ));
                }
                userAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(Blood.this, "Error getting data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSpinnerData() {
        loadUniqueValues("workstation", workstationList, R.id.spinnerWorkstation);
        loadUniqueValues("designation", designationList, R.id.spinnerDesignation);
        loadUniqueValues("division", divisionList, R.id.spinnerDivision);
        loadUniqueValues("district", districtList, R.id.spinnerDistrict);
        loadUniqueValues("upozila", upozilaList, R.id.spinnerUpozila);
        loadUniqueValues("blood", bloodList, R.id.spinnerBlood);
    }

    private void loadUniqueValues(String field, List<String> list, int spinnerId) {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                list.clear();

                // Customize the default text based on the field
                String defaultText = field.equals("blood") ? "Select Blood Group" : "Select " + field.substring(0, 1).toUpperCase() + field.substring(1);
                list.add(defaultText);

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String value = document.getString(field);
                    if (value != null && !list.contains(value)) {
                        list.add(value);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ((Spinner) findViewById(spinnerId)).setAdapter(adapter);
            } else {
                Toast.makeText(Blood.this, "Error loading " + field + " data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class User {
        private String name, workstation, designation, district, division, upozila, blood, number;

        public User(String name, String workstation, String designation, String district, String division, String upozila, String number, String blood) {
            this.name = name;
            this.workstation = workstation;
            this.designation = designation;
            this.district = district;
            this.division = division;
            this.upozila = upozila;
            this.number = number;
            this.blood = blood;
        }

        public String getName() { return name; }
        public String getWorkstation() { return workstation; }
        public String getDesignation() { return designation; }
        public String getDistrict() { return district; }
        public String getDivision() { return division; }
        public String getUpozila() { return upozila; }
        public String getNumber() { return number; }
        public String getBlood() { return blood; }

    }

    public class UserAdapter extends ArrayAdapter<User> {
        private Context context;
        private List<User> userList;

        public UserAdapter(Context context, List<User> userList) {
            super(context, 0, userList);
            this.context = context;
            this.userList = userList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);
            }

            User user = userList.get(position);

            TextView name = convertView.findViewById(R.id.textViewName);
            TextView workstation = convertView.findViewById(R.id.textViewWorkstation);
            TextView designation = convertView.findViewById(R.id.textViewDesignation);
            TextView district = convertView.findViewById(R.id.textViewDistrict);
            TextView division = convertView.findViewById(R.id.textViewDivision);
            TextView upozila = convertView.findViewById(R.id.textViewUpozila);
            TextView number = convertView.findViewById(R.id.textViewNumber);

            TextView blood = convertView.findViewById(R.id.textViewBlood);

            name.setText("Name: " + user.getName());
            workstation.setText("Workstation: " + user.getWorkstation());
            designation.setText("Designation: " + user.getDesignation());
            district.setText("District: " + user.getDistrict());
            division.setText("Division: " + user.getDivision());
            upozila.setText("Upozila: " + user.getUpozila());
            number.setText("Number: " + user.getNumber());
            blood.setText("Blood Group: " + user.getBlood());

            return convertView;
        }
    }

}
