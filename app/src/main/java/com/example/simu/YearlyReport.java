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
import android.widget.EditText;
import android.widget.LinearLayout;
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
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.util.Calendar;

public class YearlyReport extends AppCompatActivity {

    private EditText editTextDate;
    Button toggleSpinnersBtn;
    LinearLayout spinnerLayout;
    private Calendar calendar;
    private String selectedDate = "";
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserAttendanceGrouped> userAttendanceGroupedList;
    private FirebaseFirestore db;
    private Button buttonDownload;
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
        setContentView(R.layout.activity_yearly_report);

        editTextDate = findViewById(R.id.editTextDate);
        calendar = Calendar.getInstance();

        spinnerLayout = findViewById(R.id.spinnerLayout);
        toggleSpinnersBtn = findViewById(R.id.toggleSpinnersBtn);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAttendanceGroupedList = new ArrayList<>();
        userAdapter = new UserAdapter(userAttendanceGroupedList);
        recyclerView.setAdapter(userAdapter);
        buttonDownload = findViewById(R.id.download_button);

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

        db = FirebaseFirestore.getInstance();
        loadUsersAndAttendanceFromFirestore();

        buttonDownload = findViewById(R.id.download_button);
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
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdf();
            }
        });

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
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

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        selectedDate = formatDate(calendar.getTimeInMillis());
                        editTextDate.setText(selectedDate);
                        filterData();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void createPdf() {
        PdfDocument document = new PdfDocument();
        Paint paint = new Paint();
        int pageWidth = 595; // Standard A4 width in points
        int pageHeight = 842; // Standard A4 height in points
        int margin = 10;
        int y = 25;

        paint.setTextSize(15f);
        paint.setFakeBoldText(true);

        // Function to start a new page
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Draw the title on the first page
        canvas.drawText("Yearly Attendance Report", margin, y, paint);
        y += paint.descent() - paint.ascent() + 20;

        paint.setTextSize(10f);
        paint.setFakeBoldText(false);

        for (UserAttendanceGrouped userAttendanceGrouped : userAttendanceGroupedList) {
            // Check if there is enough space for the next content; if not, start a new page
            if (y + (paint.descent() - paint.ascent() + 20) > pageHeight - margin) {
                document.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, document.getPages().size() + 1).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                y = margin + 25; // Reset y for new page
            }

            paint.setFakeBoldText(true);
            canvas.drawText("Date: " + userAttendanceGrouped.getDate(), margin, y, paint);
            y += paint.descent() - paint.ascent() + 10;

            for (UserAttendance userAttendance : userAttendanceGrouped.getUserAttendances()) {
                // Check if there is enough space for the next content
                if (y + (paint.descent() - paint.ascent() * 5 + 20) > pageHeight - margin) {
                    document.finishPage(page);
                    pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, document.getPages().size() + 1).create();
                    page = document.startPage(pageInfo);
                    canvas = page.getCanvas();
                    y = margin + 25; // Reset y for new page
                }

                User user = userAttendance.getUser();
                Attendance attendance = userAttendance.getAttendance();

                paint.setFakeBoldText(false);
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

        // Finish the last page
        document.finishPage(page);

        // Save the document to a file
        File pdfDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "YearlyReports");
        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        String fileName = "YearlyReport_" + System.currentTimeMillis() + ".pdf";
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
                        } else {
                            // Handle error
                        }
                    }
                });
    }

    private void loadAttendanceFromFirestore(final Map<String, User> userMap) {
        db.collection("Attendance")
                .whereGreaterThanOrEqualTo("postingTime", getStartOfYear())
                .whereLessThanOrEqualTo("postingTime", getEndOfYear())
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

    private long getStartOfYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getEndOfYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
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

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private void filterData() {
        String selectedWorkstation = spinnerWorkstation.getSelectedItem().toString();
        String selectedDesignation = spinnerDesignation.getSelectedItem().toString();
        String selectedDivision = spinnerDivision.getSelectedItem().toString();
        String selectedDistrict = spinnerDistrict.getSelectedItem().toString();
        String selectedUpozila = spinnerUpozila.getSelectedItem().toString();

        List<UserAttendanceGrouped> filteredList = new ArrayList<>();
        for (UserAttendanceGrouped userAttendanceGrouped : userAttendanceGroupedList) {
            if (userAttendanceGrouped.getDate().equals(selectedDate) || selectedDate.isEmpty()) {
                List<UserAttendance> filteredUserAttendances = new ArrayList<>();
                for (UserAttendance userAttendance : userAttendanceGrouped.getUserAttendances()) {
                    User user = userAttendance.getUser();
                    boolean addToFilteredList = true;

                    if (!selectedWorkstation.equals("Workstation") && !user.getWorkstation().equals(selectedWorkstation)) {
                        addToFilteredList = false;
                    }

                    if (!selectedDesignation.equals("Designation") && !user.getDesignation().equals(selectedDesignation)) {
                        addToFilteredList = false;
                    }

                    if (!selectedDivision.equals("Division") && !user.getDivision().equals(selectedDivision)) {
                        addToFilteredList = false;
                    }

                    if (!selectedDistrict.equals("District") && !user.getDistrict().equals(selectedDistrict)) {
                        addToFilteredList = false;
                    }

                    if (!selectedUpozila.equals("Upazila") && !user.getUpozila().equals(selectedUpozila)) {
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