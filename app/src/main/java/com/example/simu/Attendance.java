package com.example.simu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Objects;

public class Attendance extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    Button buttonViewUsers, approve;

    Button activites_feed, intime, late, approved_leave, training, urgent, exit1, exit2, exitformorningshift, dailyReport, monthlyReport, yearlyReport,
            earned_leave, extraordinary_leave, study_leave, leave_not_due, post_retirement_leave, casual_leave, public_and_gov_holiday,
            public_holiday, government_holiday, optional_leave, rest_and_recreation_leave, special_disability_leave, special_sick_leave,
            leave_of_vacation_dept, departmental_leave, hospital_leave, compulsory_leave, leave_without_pay, quarantine_leave, maternity_leave;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fstore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.CAMERA
            }, REQUEST_LOCATION_PERMISSION);
            return;
        }

        activites_feed = findViewById(R.id.activites_feed);
        intime = findViewById(R.id.entry);
        late = findViewById(R.id.late);
        approved_leave = findViewById(R.id.approved_leave);
        training = findViewById(R.id.training);
        urgent = findViewById(R.id.urgent);
        exit1 = findViewById(R.id.exit1);
        exit2 = findViewById(R.id.exit2);
        exitformorningshift = findViewById(R.id.exitformorningshift);
        earned_leave = findViewById(R.id.earned_leave);
        extraordinary_leave = findViewById(R.id.extraordinary_leave);
        study_leave = findViewById(R.id.study_leave);
        leave_not_due = findViewById(R.id.leave_not_due);
        post_retirement_leave = findViewById(R.id.post_retirement_leave);
        casual_leave = findViewById(R.id.casual_leave);
        public_and_gov_holiday = findViewById(R.id.public_and_gov_holiday);
        public_holiday = findViewById(R.id.public_holiday);
        government_holiday = findViewById(R.id.government_holiday);
        optional_leave = findViewById(R.id.optional_leave);
        rest_and_recreation_leave = findViewById(R.id.rest_and_recreation_leave);
        special_disability_leave = findViewById(R.id.special_disability_leave);
        special_sick_leave = findViewById(R.id.special_sick_leave);
        leave_of_vacation_dept = findViewById(R.id.leave_of_vacation_dept);
        departmental_leave = findViewById(R.id.departmental_leave);
        hospital_leave = findViewById(R.id.hospital_leave);
        compulsory_leave = findViewById(R.id.compulsory_leave);
        leave_without_pay = findViewById(R.id.leave_without_pay);
        quarantine_leave = findViewById(R.id.quarantine_leave);
        maternity_leave = findViewById(R.id.maternity_leave);
        dailyReport = findViewById(R.id.daily_report);
        monthlyReport = findViewById(R.id.monthly_report);
        yearlyReport = findViewById(R.id.yearly_Report);
        approve = findViewById(R.id.approve);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        String userId = fAuth.getCurrentUser().getUid();

        buttonViewUsers = findViewById(R.id.buttonViewUsers);


        activites_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Attendance.this, ActivitiesFeed.class));
            }
        });

        intime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIntimeValidity();
            }
        });

        late.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLateValidity();
            }
        });

        exitformorningshift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkExitForMorningValidity();
            }
        });

        exit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkExit1Validity();
            }
        });

        exit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkExit2Validity();
            }
        });

        approved_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Approved leave");
            }
        });

        urgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Urgent leave");
            }
        });

        training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Training");
            }
        });

        earned_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Earned Leave");
            }
        });

        extraordinary_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Extraordinary Leave");
            }
        });

        study_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Study Leave");
            }
        });

        leave_not_due.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Leave Not Due");
            }
        });

        post_retirement_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Post Retirement Leave");
            }
        });

        casual_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithLimit("Casual Leave");
            }
        });

        public_and_gov_holiday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Public and Government Holiday");
            }
        });

        public_holiday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Public Holiday");
            }
        });

        government_holiday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Government Holiday");
            }
        });

        optional_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Optional Leave");
            }
        });

        rest_and_recreation_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Rest and Recreation Leave");
            }
        });

        special_disability_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Special Disability Leave");
            }
        });

        special_sick_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Special Sick Leave");
            }
        });

        leave_of_vacation_dept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Leave of Vacation Department");
            }
        });

        departmental_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Departmental Leave");
            }
        });

        hospital_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Hospital Leave");
            }
        });

        compulsory_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Compulsory Leave");
            }
        });

        leave_without_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Leave Without Pay");
            }
        });

        quarantine_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Quarantine Leave");
            }
        });

        maternity_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("Maternity Leave");
            }
        });

        dailyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fstore.collection("users").document(fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if ((Objects.equals(document.getString("isAdmin"), "Yes")) || (Objects.equals(document.getString("head"), "Yes"))) {
                                startActivity(new Intent(Attendance.this, DailyReport.class));
                            } else {
                                Toast.makeText(Attendance.this, "Only Admins Can See Daily Report", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        monthlyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fstore.collection("users").document(fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if ((Objects.equals(document.getString("isAdmin"), "Yes")) || (Objects.equals(document.getString("head"), "Yes"))) {
                                startActivity(new Intent(Attendance.this, MonthlyReport.class));
                            } else {
                                Toast.makeText(Attendance.this, "Only Admins Can See Monthly Report", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        yearlyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fstore.collection("users").document(fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if ((Objects.equals(document.getString("isAdmin"), "Yes")) || (Objects.equals(document.getString("head"), "Yes"))) {
                                startActivity(new Intent(Attendance.this, YearlyReport.class));
                            } else {
                                Toast.makeText(Attendance.this, "Only Admins Can See Yearly Report", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        buttonViewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fstore.collection("users").document(fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if ((Objects.equals(document.getString("isAdmin"), "Yes")) || (Objects.equals(document.getString("head"), "Yes"))) {
                                startActivity(new Intent(Attendance.this, UserListActivity.class));
                            } else {
                                Toast.makeText(Attendance.this, "Only Admins Can See this", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }
        });

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fstore.collection("users").document(fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if ((Objects.equals(document.getString("isAdmin"), "Yes")) || (Objects.equals(document.getString("head"), "Yes"))) {
                                startActivity(new Intent(Attendance.this, Approve.class));
                            } else {
                                Toast.makeText(Attendance.this, "Only Admins Can Approve Users", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    private void checkIntimeValidity() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NTPUDPClient client = new NTPUDPClient();
                    client.setDefaultTimeout(5000);

                    String[] ntpServers = {
                            "pool.ntp.org",
                            "time.google.com",
                            "time.windows.com",
                            "time.nist.gov"
                    };

                    long currentTime = -1;
                    for (String server : ntpServers) {
                        try {
                            InetAddress inetAddress = InetAddress.getByName(server);
                            TimeInfo timeInfo = client.getTime(inetAddress);
                            currentTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                            break;
                        } catch (Exception e) {
                            Log.e("NTP", "Failed to fetch time from " + server, e);
                        }
                    }
                    if (currentTime == -1) {
                        currentTime = System.currentTimeMillis();
                        Log.w("NTP", "Using system time as fallback");
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentTime);

                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    if (hour == 7 || (hour == 9 && minute <= 15)) {
                        runOnUiThread(() -> {
                            intime.setEnabled(true);
                            startActivityWithoutLeave("Intime");
                        });
                    } else {
                        runOnUiThread(() -> {
                            intime.setEnabled(false);
                            Toast.makeText(Attendance.this, "You are late!!!", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (Exception e) {
                    Log.e("NTP", "Error fetching time", e);
                    runOnUiThread(() -> Toast.makeText(Attendance.this, "Unable to validate time. Please try again later.", Toast.LENGTH_LONG).show());
                }
            }
        });
        thread.start();
    }


    private void checkLateValidity() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NTPUDPClient client = new NTPUDPClient();
                    client.setDefaultTimeout(5000);

                    String[] ntpServers = {
                            "pool.ntp.org",
                            "time.google.com",
                            "time.windows.com",
                            "time.nist.gov"
                    };

                    long currentTime = -1;

                    for (String server : ntpServers) {
                        try {
                            InetAddress inetAddress = InetAddress.getByName(server);
                            TimeInfo timeInfo = client.getTime(inetAddress);
                            currentTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                            break;
                        } catch (Exception e) {
                            Log.e("NTP", "Failed to fetch time from " + server, e);
                        }
                    }
                    if (currentTime == -1) {
                        currentTime = System.currentTimeMillis();
                        Log.w("NTP", "Using system time as fallback");
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentTime);

                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    if ((hour == 9 && minute >= 16) || (hour == 10 && minute == 0)) {
                        runOnUiThread(() -> {
                            late.setEnabled(true);
                            startActivityWithoutLeave("Late");
                        });
                    } else {
                        runOnUiThread(() -> {
                            late.setEnabled(false);
                            Toast.makeText(Attendance.this, "Time is over!!!", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (Exception e) {
                    Log.e("NTP", "Error in checkLateValidity", e);
                    runOnUiThread(() -> Toast.makeText(Attendance.this, "Unable to validate time. Please try again later.", Toast.LENGTH_LONG).show());
                }
            }
        });
        thread.start();
    }


    private void checkExitForMorningValidity() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NTPUDPClient client = new NTPUDPClient();
                    client.setDefaultTimeout(5000);

                    String[] ntpServers = {
                            "pool.ntp.org",
                            "time.google.com",
                            "time.windows.com",
                            "time.nist.gov"
                    };

                    long currentTime = -1;

                    for (String server : ntpServers) {
                        try {
                            InetAddress inetAddress = InetAddress.getByName(server);
                            TimeInfo timeInfo = client.getTime(inetAddress);
                            currentTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                            break;
                        } catch (Exception e) {
                            Log.e("NTP", "Failed to fetch time from " + server, e);
                        }
                    }
                    if (currentTime == -1) {
                        currentTime = System.currentTimeMillis();
                        Log.w("NTP", "Using system time as fallback");
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentTime);

                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    if (hour > 11 || (hour == 11 && minute > 30)) {
                        runOnUiThread(() -> {
                            exitformorningshift.setEnabled(true);
                            startActivityWithoutLeave("Exit");
                        });
                    } else {
                        runOnUiThread(() -> {
                            exitformorningshift.setEnabled(false);
                            Toast.makeText(Attendance.this, "Click after 11:30 AM", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (Exception e) {
                    Log.e("NTP", "Error in checkExitForMorningValidity", e);
                    runOnUiThread(() -> Toast.makeText(Attendance.this, "Unable to validate time. Please try again later.", Toast.LENGTH_LONG).show());
                }
            }
        });
        thread.start();
    }


    private void checkExit1Validity() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NTPUDPClient client = new NTPUDPClient();
                    client.setDefaultTimeout(5000); // Set a 5-second timeout for NTP requests

                    String[] ntpServers = {
                            "pool.ntp.org",
                            "time.google.com",
                            "time.windows.com",
                            "time.nist.gov"
                    };

                    long currentTime = -1;
                    for (String server : ntpServers) {
                        try {
                            InetAddress inetAddress = InetAddress.getByName(server);
                            TimeInfo timeInfo = client.getTime(inetAddress);
                            currentTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                            break;
                        } catch (Exception e) {
                            Log.e("NTP", "Failed to fetch time from " + server, e);
                        }
                    }
                    if (currentTime == -1) {
                        currentTime = System.currentTimeMillis();
                        Log.w("NTP", "Using system time as fallback");
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentTime);

                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    if (hour > 15 || (hour == 15 && minute > 0)) {
                        runOnUiThread(() -> {
                            exit1.setEnabled(true);
                            startActivityWithoutLeave("Exit");
                        });
                    } else {
                        runOnUiThread(() -> {
                            exit1.setEnabled(false);
                            Toast.makeText(Attendance.this, "Click after 3 PM.", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (Exception e) {
                    Log.e("NTP", "Error in checkExit1Validity", e);
                    runOnUiThread(() -> Toast.makeText(Attendance.this, "Unable to validate time. Please try again later.", Toast.LENGTH_LONG).show());
                }
            }
        });
        thread.start();
    }


    private void checkExit2Validity() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NTPUDPClient client = new NTPUDPClient();
                    client.setDefaultTimeout(5000);

                    String[] ntpServers = {
                            "pool.ntp.org",
                            "time.google.com",
                            "time.windows.com",
                            "time.nist.gov"
                    };

                    long currentTime = -1;
                    for (String server : ntpServers) {
                        try {
                            InetAddress inetAddress = InetAddress.getByName(server);
                            TimeInfo timeInfo = client.getTime(inetAddress);
                            currentTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                            break;
                        } catch (Exception e) {
                            Log.e("NTP", "Failed to fetch time from " + server, e);
                        }
                    }
                    if (currentTime == -1) {
                        currentTime = System.currentTimeMillis();
                        Log.w("NTP", "Using system time as fallback");
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentTime);

                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    if (hour > 17 || (hour == 17 && minute > 0)) {
                        runOnUiThread(() -> {
                            exit2.setEnabled(true);
                            startActivityWithoutLeave("Exit");
                        });
                    } else {
                        runOnUiThread(() -> {
                            exit2.setEnabled(false);
                            Toast.makeText(Attendance.this, "Click after 5 PM.", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (Exception e) {
                    Log.e("NTP", "Error in checkExit2Validity", e);
                    runOnUiThread(() -> Toast.makeText(Attendance.this, "Unable to validate time. Please try again later.", Toast.LENGTH_LONG).show());
                }
            }
        });
        thread.start();
    }


    private void startActivityWithoutLeave(String attendanceType) {
        Intent intent = new Intent(Attendance.this, Upload_attendance.class);
        intent.putExtra("attendanceType", attendanceType);
        startActivity(intent);
    }

    private void startActivityWithLimit(String attendanceType) {

        String userId = fAuth.getCurrentUser().getUid();

        fstore.collection("UserLeaveRecords")
                .document(userId + "_" + attendanceType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                int totalDays = Objects.requireNonNull(document.getLong("totalDays")).intValue();
                                int leaveLimit = 0;
                                if (attendanceType.equals("Casual Leave")) {
                                    leaveLimit = 20;
                                } else if (attendanceType.equals("Maternity Leave")) {
                                    leaveLimit = 180;
                                }

                                if (totalDays >= leaveLimit) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Attendance.this, "You have exceeded the limit for " + attendanceType + ".", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    showEnterDaysDialog(attendanceType, totalDays, leaveLimit);
                                }

                            } else {
                                int leaveLimit = attendanceType.equals("Casual Leave") ? 20 : attendanceType.equals("Maternity Leave") ? 180 : 0;
                                showEnterDaysDialog(attendanceType, 0, leaveLimit);
                            }

                        } else {
                            startActivityWithAttendanceType("Casual Leave");
                        }
                    }
                });
    }

    private void showEnterDaysDialog(String attendanceType, int totalDays, int leaveLimit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Attendance.this);
        builder.setTitle("Enter Number of Days");

        final EditText input = new EditText(Attendance.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String numberOfDaysStr = input.getText().toString();

                if (!numberOfDaysStr.isEmpty()) {
                    int numberOfDays = Integer.parseInt(numberOfDaysStr);

                    if (totalDays + numberOfDays > leaveLimit) {
                        Toast.makeText(Attendance.this, "You cannot take more than " + leaveLimit + " days for " + attendanceType + ".", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(Attendance.this, Upload_attendance.class);
                        intent.putExtra("attendanceType", attendanceType);
                        intent.putExtra("numberOfDays", String.valueOf(numberOfDays));
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(Attendance.this, "Please enter a valid number of days", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void startActivityWithAttendanceType(String attendanceType) {


        AlertDialog.Builder builder = new AlertDialog.Builder(Attendance.this);
        builder.setTitle("Enter Number of Days");

        final EditText input = new EditText(Attendance.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String numberOfDays = input.getText().toString();

                if (!numberOfDays.isEmpty()) {
                    Intent intent = new Intent(Attendance.this, Upload_attendance.class);
                    intent.putExtra("attendanceType", attendanceType);
                    intent.putExtra("numberOfDays", numberOfDays);
                    startActivity(intent);
                } else {
                    Toast.makeText(Attendance.this, "Please enter a valid number of days", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Location permission is required.", Toast.LENGTH_SHORT).show();
            }

            if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}