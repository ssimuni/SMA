package com.example.simu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DailyReport extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FirebaseFirestore db;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance();
        loadUsersAndAttendanceFromFirestore();

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

        spinnerWorkstation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerDesignation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerUpozila.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadUsersAndAttendanceFromFirestore() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String, User> userMap = new HashMap<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                String userId = document.getId();
                                String name = document.getString("name");
                                String designation = document.getString("designation");
                                String workstation = document.getString("workstation");
                                String division = document.getString("division");
                                String district = document.getString("district");
                                String upozila = document.getString("upozila");

                                User user = new User(userId, name, designation, workstation, division, district, upozila);
                                userMap.put(userId, user);

                                if (!workstationList.contains(workstation)) {
                                    workstationList.add(workstation);
                                }
                                if (!designationList.contains(designation)) {
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
                            loadAttendanceFromFirestore(userMap);
                            setSpinnerAdapters();
                        } else {
                            // Handle error
                        }
                    }
                });
    }

    private void loadAttendanceFromFirestore(final Map<String, User> userMap) {
        db.collection("Attendance")
                .whereGreaterThanOrEqualTo("postingTime", getStartOfDay())
                .whereLessThanOrEqualTo("postingTime", getEndOfDay())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = document.getString("userId");
                                String attendanceType = document.getString("attendanceType");
                                long postingTime = document.getLong("postingTime");

                                User user = userMap.get(userId);
                                if (user != null) {
                                    user.addAttendance(new Attendance(attendanceType, postingTime));
                                }
                            }
                            userList.addAll(userMap.values());
                            userAdapter.notifyDataSetChanged();
                            filterData();
                        } else {
                            // Handle error
                        }
                    }
                });
    }

    private long getStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getEndOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    private void filterData() {
        String selectedWorkstation = spinnerWorkstation.getSelectedItem().toString();
        String selectedDesignation = spinnerDesignation.getSelectedItem().toString();
        String selectedDivision = spinnerDivision.getSelectedItem().toString();
        String selectedDistrict = spinnerDistrict.getSelectedItem().toString();
        String selectedUpozila = spinnerUpozila.getSelectedItem().toString();

        List<User> filteredList = new ArrayList<>();
        for (User user : userList) {
            boolean matchWorkstation = selectedWorkstation.equals("Workstation") || user.getWorkstation().equals(selectedWorkstation);
            boolean matchDesignation = selectedDesignation.equals("Designation") || user.getDesignation().equals(selectedDesignation);
            boolean matchDivision = selectedDivision.equals("Division") || user.getDivision().equals(selectedDivision);
            boolean matchDistrict = selectedDistrict.equals("District") || user.getDistrict().equals(selectedDistrict);
            boolean matchUpozila = selectedUpozila.equals("Upazila") || user.getUpozila().equals(selectedUpozila);

            if (matchWorkstation && matchDesignation && matchDivision && matchDistrict && matchUpozila) {
                filteredList.add(user);
            }
        }
        userAdapter.updateList(filteredList);
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

    private static class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

        private List<User> userList;

        public UserAdapter(List<User> userList) {
            this.userList = userList;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_report_table, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = userList.get(position);
            holder.textViewName.setText(user.getName());
            holder.textViewDesignation.setText(user.getDesignation());
            holder.textViewWorkstation.setText(user.getWorkstation());

            holder.intime.setText("");
            holder.late.setText("");
            holder.exit.setText("");
            holder.approved_leave.setText("");
            holder.training.setText("");
            holder.urgent.setText("");

            for (Attendance attendance : user.getAttendanceList()) {
                String formattedTime = formatTime(attendance.getPostingTime());
                switch (attendance.getAttendanceType()) {
                    case "Intime":
                        holder.intime.setText(formattedTime);
                        break;
                    case "Late":
                        holder.late.setText(formattedTime);
                        break;
                    case "Exit":
                        holder.exit.setText(formattedTime);
                        break;
                    case "Approved leave":
                        holder.approved_leave.setText(formattedTime);
                        break;
                    case "Urgent leave":
                        holder.urgent.setText(formattedTime);
                        break;
                    case "Training":
                        holder.training.setText(formattedTime);
                        break;
                }
            }
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public void updateList(List<User> newList) {
            userList = newList;
            notifyDataSetChanged();
        }

        public static class UserViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName, textViewDesignation, textViewWorkstation, intime, late, exit, approved_leave, training, urgent;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textViewName);
                textViewDesignation = itemView.findViewById(R.id.textViewDesignation);
                textViewWorkstation = itemView.findViewById(R.id.textViewWorkstation);
                intime = itemView.findViewById(R.id.intime);
                late = itemView.findViewById(R.id.late);
                exit = itemView.findViewById(R.id.exit);
                approved_leave = itemView.findViewById(R.id.approved_leave);
                training = itemView.findViewById(R.id.training);
                urgent = itemView.findViewById(R.id.urgent);
            }
        }
    }

    private static class User {
        private String userId;
        private String name;
        private String designation;
        private String workstation;
        private  String division;
        private String district;
        private String upozila;
        private List<Attendance> attendanceList;

        public User(String userId, String name, String designation, String workstation, String division, String district, String upozila) {
            this.userId = userId;
            this.name = name;
            this.designation = designation;
            this.workstation = workstation;
            this.attendanceList = new ArrayList<>();
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

        public List<Attendance> getAttendanceList() {
            return attendanceList;
        }

        public void addAttendance(Attendance attendance) {
            attendanceList.add(attendance);
        }
    }

    private static class Attendance {
        private String attendanceType;
        private long postingTime;

        public Attendance(String attendanceType, long postingTime) {
            this.attendanceType = attendanceType;
            this.postingTime = postingTime;
        }

        public String getAttendanceType() {
            return attendanceType;
        }

        public long getPostingTime() {
            return postingTime;
        }
    }

    private static String formatTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date(timeInMillis);
        return sdf.format(date);
    }
}
