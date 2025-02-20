package com.example.simu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;

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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class DailyReport extends AppCompatActivity {
    Button toggleSpinnersBtn;
    LinearLayout spinnerLayout;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FirebaseFirestore db;
    private Spinner spinnerWorkstation, spinnerDesignation, spinnerDivision, spinnerDistrict, spinnerUpozila;
    private List<String> workstationList, designationList, divisionList, districtList, upozilaList;
    private Button btnDownloadPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf);

        spinnerLayout = findViewById(R.id.spinnerLayout);
        toggleSpinnersBtn = findViewById(R.id.toggleSpinnersBtn);

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
        btnDownloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdf();
            }
        });

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

    private void createPdf() {
        PdfDocument document = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int y = 25;
        int margin = 10;

        paint.setTextSize(15f);
        paint.setFakeBoldText(true);
        canvas.drawText("Daily Attendance Report", margin, y, paint);
        y += paint.descent() - paint.ascent() + 10;

        paint.setTextSize(10f);
        paint.setFakeBoldText(false);

        for (User user : userList) {
            // Skip users with empty attendance list
            if (user.getAttendanceList().isEmpty()) {
                continue;
            }

            paint.setFakeBoldText(true);

            canvas.drawText("Name: " + user.getName(), margin, y, paint);
            y += paint.descent() - paint.ascent();

            canvas.drawText("Designation: " + user.getDesignation(), margin, y, paint);
            y += paint.descent() - paint.ascent();

            canvas.drawText("Workstation: " + user.getWorkstation(), margin, y, paint);
            y += paint.descent() - paint.ascent();

            canvas.drawText("Division: " + user.getDivision(), margin, y, paint);
            y += paint.descent() - paint.ascent();

            canvas.drawText("District: " + user.getDistrict(), margin, y, paint);
            y += paint.descent() - paint.ascent();

            canvas.drawText("Upazila: " + user.getUpozila(), margin, y, paint);
            y += paint.descent() - paint.ascent();

            for (Attendance attendance : user.getAttendanceList()) {
                String formattedTime = formatTime(attendance.getPostingTime());
                canvas.drawText("Time: " + formattedTime, margin, y, paint);
                y += paint.descent() - paint.ascent();
                canvas.drawText("Type: " + attendance.getAttendanceType(), margin, y, paint);
                y += 20;
            }

            y += 20; // Additional space between users
        }

        document.finishPage(page);

        File pdfDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "DailyReports");
        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        String fileName = "DailyReport_" + System.currentTimeMillis() + ".pdf";
        File file = new File(pdfDir, fileName);

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF saved to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            document.close();
        }
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
                            loadAttendanceFromFirestore(userMap);
                            setSpinnerAdapters();
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
                            List<User> usersWithAttendance = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = document.getString("userId");
                                String attendanceType = document.getString("attendanceType");
                                long postingTime = document.getLong("postingTime");

                                User user = userMap.get(userId);
                                if (user != null) {
                                    user.addAttendance(new Attendance(attendanceType, postingTime));

                                    if (!usersWithAttendance.contains(user)) {
                                        usersWithAttendance.add(user);
                                    }
                                }
                            }
                            userList.clear();
                            userList.addAll(usersWithAttendance);
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

            if (holder.intime != null) holder.intime.setText("");
            if (holder.late != null) holder.late.setText("");
            if (holder.exit != null) holder.exit.setText("");
            if (holder.approved_leave != null) holder.approved_leave.setText("");
            if (holder.training != null) holder.training.setText("");
            if (holder.urgent != null) holder.urgent.setText("");

            if (holder.earned_leave != null) holder.earned_leave.setText("");
            if (holder.extraordinary_leave != null) holder.extraordinary_leave.setText("");
            if (holder.study_leave != null) holder.study_leave.setText("");
            if (holder.leave_not_due != null) holder.leave_not_due.setText("");
            if (holder.post_retirement_leave != null) holder.post_retirement_leave.setText("");
            if (holder.casual_leave != null) holder.casual_leave.setText("");
            if (holder.public_and_gov_holiday != null) holder.public_and_gov_holiday.setText("");
            if (holder.public_holiday != null) holder.public_holiday.setText("");
            if (holder.government_holiday != null) holder.government_holiday.setText("");
            if (holder.optional_leave != null) holder.optional_leave.setText("");
            if (holder.rest_and_recreation_leave != null) holder.rest_and_recreation_leave.setText("");
            if (holder.special_disability_leave != null) holder.special_disability_leave.setText("");
            if (holder.special_sick_leave != null) holder.special_sick_leave.setText("");
            if (holder.leave_of_vacation_dept != null) holder.leave_of_vacation_dept.setText("");
            if (holder.departmental_leave != null) holder.departmental_leave.setText("");
            if (holder.hospital_leave != null) holder.hospital_leave.setText("");
            if (holder.compulsory_leave != null) holder.compulsory_leave.setText("");
            if (holder.leave_without_pay != null) holder.leave_without_pay.setText("");
            if (holder.quarantine_leave != null) holder.quarantine_leave.setText("");
            if (holder.maternity_leave != null) holder.maternity_leave.setText("");

            for (Attendance attendance : user.getAttendanceList()) {
                String formattedTime = formatTime(attendance.getPostingTime());
                switch (attendance.getAttendanceType()) {
                    case "Intime":
                        if (holder.intime != null) holder.intime.setText(formattedTime);
                        break;
                    case "Late":
                        if (holder.late != null) holder.late.setText(formattedTime);
                        break;
                    case "Exit":
                        if (holder.exit != null) holder.exit.setText(formattedTime);
                        break;
                    case "Approved leave":
                        if (holder.approved_leave != null) holder.approved_leave.setText(formattedTime);
                        break;
                    case "Urgent leave":
                        if (holder.urgent != null) holder.urgent.setText(formattedTime);
                        break;
                    case "Training":
                        if (holder.training != null) holder.training.setText(formattedTime);
                        break;
                    case "Earned Leave":
                        if (holder.earned_leave != null) holder.earned_leave.setText(formattedTime);
                        break;
                    case "Extraordinary Leave":
                        if (holder.extraordinary_leave != null) holder.extraordinary_leave.setText(formattedTime);
                        break;
                    case "Study Leave":
                        if (holder.study_leave != null) holder.study_leave.setText(formattedTime);
                        break;
                    case "Leave Not Due":
                        if (holder.leave_not_due != null) holder.leave_not_due.setText(formattedTime);
                        break;
                    case "Post Retirement Leave":
                        if (holder.post_retirement_leave != null) holder.post_retirement_leave.setText(formattedTime);
                        break;
                    case "Casual Leave":
                        if (holder.casual_leave != null) holder.casual_leave.setText(formattedTime);
                        break;
                    case "Public and Government Holiday":
                        if (holder.public_and_gov_holiday != null) holder.public_and_gov_holiday.setText(formattedTime);
                        break;
                    case "Public Holiday":
                        if (holder.public_holiday != null) holder.public_holiday.setText(formattedTime);
                        break;
                    case "Government Holiday":
                        if (holder.government_holiday != null) holder.government_holiday.setText(formattedTime);
                        break;
                    case "Optional Leave":
                        if (holder.optional_leave != null) holder.optional_leave.setText(formattedTime);
                        break;
                    case "Rest and Recreation Leave":
                        if (holder.rest_and_recreation_leave != null) holder.rest_and_recreation_leave.setText(formattedTime);
                        break;
                    case "Special Disability Leave":
                        if (holder.special_disability_leave != null) holder.special_disability_leave.setText(formattedTime);
                        break;
                    case "Special Sick Leave":
                        if (holder.special_sick_leave != null) holder.special_sick_leave.setText(formattedTime);
                        break;
                    case "Leave of Vacation Department":
                        if (holder.leave_of_vacation_dept != null) holder.leave_of_vacation_dept.setText(formattedTime);
                        break;
                    case "Departmental Leave":
                        if (holder.departmental_leave != null) holder.departmental_leave.setText(formattedTime);
                        break;
                    case "Hospital Leave":
                        if (holder.hospital_leave != null) holder.hospital_leave.setText(formattedTime);
                        break;
                    case "Compulsory Leave":
                        if (holder.compulsory_leave != null) holder.compulsory_leave.setText(formattedTime);
                        break;
                    case "Leave Without Pay":
                        if (holder.leave_without_pay != null) holder.leave_without_pay.setText(formattedTime);
                        break;
                    case "Quarantine Leave":
                        if (holder.quarantine_leave != null) holder.quarantine_leave.setText(formattedTime);
                        break;
                    case "Maternity Leave":
                        if (holder.maternity_leave != null) holder.maternity_leave.setText(formattedTime);
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
            TextView earned_leave, extraordinary_leave, study_leave, leave_not_due;
            TextView post_retirement_leave, casual_leave, public_and_gov_holiday;
            TextView public_holiday, government_holiday, optional_leave;
            TextView rest_and_recreation_leave, special_disability_leave;
            TextView special_sick_leave, leave_of_vacation_dept, departmental_leave;
            TextView hospital_leave, compulsory_leave, leave_without_pay;
            TextView quarantine_leave, maternity_leave;

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

                earned_leave = itemView.findViewById(R.id.earned_leave);
                extraordinary_leave = itemView.findViewById(R.id.extraordinary_leave);
                study_leave = itemView.findViewById(R.id.study_leave);
                leave_not_due = itemView.findViewById(R.id.leave_not_due);
                post_retirement_leave = itemView.findViewById(R.id.post_retirement_leave);
                casual_leave = itemView.findViewById(R.id.casual_leave);
                public_and_gov_holiday = itemView.findViewById(R.id.public_and_gov_holiday);
                public_holiday = itemView.findViewById(R.id.public_holiday);
                government_holiday = itemView.findViewById(R.id.government_holiday);
                optional_leave = itemView.findViewById(R.id.optional_leave);
                rest_and_recreation_leave = itemView.findViewById(R.id.rest_and_recreation_leave);
                special_disability_leave = itemView.findViewById(R.id.special_disability_leave);
                special_sick_leave = itemView.findViewById(R.id.special_sick_leave);
                leave_of_vacation_dept = itemView.findViewById(R.id.leave_of_vacation_dept);
                departmental_leave = itemView.findViewById(R.id.departmental_leave);
                hospital_leave = itemView.findViewById(R.id.hospital_leave);
                compulsory_leave = itemView.findViewById(R.id.compulsory_leave);
                leave_without_pay = itemView.findViewById(R.id.leave_without_pay);
                quarantine_leave = itemView.findViewById(R.id.quarantine_leave);
                maternity_leave = itemView.findViewById(R.id.maternity_leave);
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