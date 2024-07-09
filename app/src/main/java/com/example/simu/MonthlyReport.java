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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MonthlyReport extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserAttendanceGrouped> userAttendanceGroupedList;
    private FirebaseFirestore db;
    private Button buttonDownload;

    private Spinner spinnerWorkstation;
    private Spinner spinnerDesignation;
    private List<String> workstationList;
    private List<String> designationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAttendanceGroupedList = new ArrayList<>();
        userAdapter = new UserAdapter(userAttendanceGroupedList);
        recyclerView.setAdapter(userAdapter);
        buttonDownload = findViewById(R.id.download_button);

        spinnerWorkstation = findViewById(R.id.spinnerWorkstation);
        spinnerDesignation = findViewById(R.id.spinnerDesignation);
        workstationList = new ArrayList<>();
        designationList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        loadUsersAndAttendanceFromFirestore();

        buttonDownload = findViewById(R.id.download_button);
        buttonDownload.setOnClickListener(new View.OnClickListener() {
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
        canvas.drawText("Monthly Attendance Report", margin, y, paint);
        y += paint.descent() - paint.ascent()+10;

        paint.setTextSize(10f);
        paint.setFakeBoldText(false);
        for (UserAttendanceGrouped userAttendanceGrouped : userAttendanceGroupedList) {
            paint.setFakeBoldText(true);

            canvas.drawText("Date: " + userAttendanceGrouped.getDate(), margin, y, paint);
            y += paint.descent() - paint.ascent()+10;

            for (UserAttendance userAttendance : userAttendanceGrouped.getUserAttendances()) {
                User user = userAttendance.getUser();
                Attendance attendance = userAttendance.getAttendance();

                canvas.drawText("Name: " + user.getName(), margin, y, paint);
                y += paint.descent() - paint.ascent();

                canvas.drawText("Designation: " + user.getDesignation(), margin, y, paint);
                y += paint.descent() - paint.ascent();

                canvas.drawText("Workstation: " + user.getWorkstation(), margin, y, paint);
                y += paint.descent() - paint.ascent();

                String formattedTime = formatTime(attendance.getPostingTime());
                canvas.drawText("Time: " + formattedTime, margin, y, paint);
                y += paint.descent() - paint.ascent();
                canvas.drawText("Type: " + attendance.getAttendanceType(), margin, y, paint);
                y += 20;
            }
        }

        document.finishPage(page);

        File pdfDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MonthlyReports");
        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        String fileName = "MonthlyReport_" + System.currentTimeMillis() + ".pdf";
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

                                User user = new User(userId, name, designation, workstation);
                                userMap.put(userId, user);

                                if (!workstationList.contains(workstation)) {
                                    workstationList.add(workstation);
                                }
                                if (!designationList.contains(designation)) {
                                    designationList.add(designation);
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
                .whereGreaterThanOrEqualTo("postingTime", getStartOfMonth())
                .whereLessThanOrEqualTo("postingTime", getEndOfMonth())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String, List<UserAttendance>> groupedAttendance = new HashMap<>();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = document.getString("userId");
                                String attendanceType = document.getString("attendanceType");
                                long postingTime = document.getLong("postingTime");

                                User user = userMap.get(userId);
                                if (user != null) {
                                    String date = sdf.format(new Date(postingTime));
                                    if (!groupedAttendance.containsKey(date)) {
                                        groupedAttendance.put(date, new ArrayList<UserAttendance>());
                                    }
                                    groupedAttendance.get(date).add(new UserAttendance(user, new Attendance(attendanceType, postingTime)));
                                }
                            }
                            for (Map.Entry<String, List<UserAttendance>> entry : groupedAttendance.entrySet()) {
                                userAttendanceGroupedList.add(new UserAttendanceGrouped(entry.getKey(), entry.getValue()));
                            }
                            userAdapter.notifyDataSetChanged();
                            filterData();
                        } else {
                            // Handle error
                        }
                    }
                });
    }

    private long getStartOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getEndOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    private void setSpinnerAdapters() {
        // Add "All" option to workstation list
        workstationList.add(0, "All");

        ArrayAdapter<String> workstationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, workstationList);
        workstationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkstation.setAdapter(workstationAdapter);

        // Add "All" option to designation list
        designationList.add(0, "All");

        ArrayAdapter<String> designationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, designationList);
        designationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDesignation.setAdapter(designationAdapter);
    }


    private void filterData() {
        String selectedWorkstation = spinnerWorkstation.getSelectedItem().toString();
        String selectedDesignation = spinnerDesignation.getSelectedItem().toString();

        List<UserAttendanceGrouped> filteredList = new ArrayList<>();
        for (UserAttendanceGrouped userAttendanceGrouped : userAttendanceGroupedList) {
            List<UserAttendance> filteredUserAttendances = new ArrayList<>();
            for (UserAttendance userAttendance : userAttendanceGrouped.getUserAttendances()) {
                User user = userAttendance.getUser();
                boolean addToFilteredList = true;

                // Check if selectedWorkstation is "All" or matches user's workstation
                if (!selectedWorkstation.equals("All") && !user.getWorkstation().equals(selectedWorkstation)) {
                    addToFilteredList = false;
                }

                // Check if selectedDesignation is "All" or matches user's designation
                if (!selectedDesignation.equals("All") && !user.getDesignation().equals(selectedDesignation)) {
                    addToFilteredList = false;
                }

                if (addToFilteredList) {
                    filteredUserAttendances.add(userAttendance);
                }
            }
            if (!filteredUserAttendances.isEmpty()) {
                filteredList.add(new UserAttendanceGrouped(userAttendanceGrouped.getDate(), filteredUserAttendances));
            }
        }
        userAdapter.updateData(filteredList);
    }


    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    static class UserAttendanceGrouped {
        private String date;
        private List<UserAttendance> userAttendances;

        public UserAttendanceGrouped(String date, List<UserAttendance> userAttendances) {
            this.date = date;
            this.userAttendances = userAttendances;
        }

        public String getDate() {
            return date;
        }

        public List<UserAttendance> getUserAttendances() {
            return userAttendances;
        }
    }

    static class UserAttendance {
        private User user;
        private Attendance attendance;

        public UserAttendance(User user, Attendance attendance) {
            this.user = user;
            this.attendance = attendance;
        }

        public User getUser() {
            return user;
        }

        public Attendance getAttendance() {
            return attendance;
        }
    }

    static class User {
        private String userId;
        private String name;
        private String designation;
        private String workstation;

        public User(String userId, String name, String designation, String workstation) {
            this.userId = userId;
            this.name = name;
            this.designation = designation;
            this.workstation = workstation;
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
    }

    static class Attendance {
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

    static class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
        private List<UserAttendanceGrouped> userAttendanceGroupedList;

        public UserAdapter(List<UserAttendanceGrouped> userAttendanceGroupedList) {
            this.userAttendanceGroupedList = userAttendanceGroupedList;
        }

        public void updateData(List<UserAttendanceGrouped> userAttendanceGroupedList) {
            this.userAttendanceGroupedList = userAttendanceGroupedList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.monthly_report_table, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            UserAttendanceGrouped userAttendanceGrouped = userAttendanceGroupedList.get(position);
            holder.dateTextView.setText(userAttendanceGrouped.getDate());

            StringBuilder details = new StringBuilder();
            for (UserAttendance userAttendance : userAttendanceGrouped.getUserAttendances()) {
                User user = userAttendance.getUser();
                Attendance attendance = userAttendance.getAttendance();
                details.append("Name: ").append(user.getName()).append("\n")
                        .append("Designation: ").append(user.getDesignation()).append("\n")
                        .append("Workstation: ").append(user.getWorkstation()).append("\n")
                        .append("Time: ").append(formatTime(attendance.getPostingTime())).append("\n")
                        .append("Type: ").append(attendance.getAttendanceType()).append("\n\n");
            }
            holder.detailsTextView.setText(details.toString().trim());
        }

        @Override
        public int getItemCount() {
            return userAttendanceGroupedList.size();
        }

        private String formatTime(long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView dateTextView;
            TextView detailsTextView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                dateTextView = itemView.findViewById(R.id.dateTextView);
                detailsTextView = itemView.findViewById(R.id.detailsTextView);
            }
        }
    }
}
