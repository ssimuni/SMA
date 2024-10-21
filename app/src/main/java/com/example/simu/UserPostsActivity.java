package com.example.simu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserPostsActivity extends AppCompatActivity {

    private TextView textViewPosts;
    private FirebaseFirestore db;
    private String userId;
    private int intimeCount = 0;
    private int lateCount = 0;
    private int leaveCount = 0;
    private int totalCount = 0;
    TextView tvR, tvPython, tvCPP, tvLeave;
    PieChart pieChart;

    private static final List<String> leaveTypes = Arrays.asList(
            "approved leave", "urgent leave", "training", "earned leave", "extraordinary leave",
            "study leave", "leave not due", "post retirement leave", "casual leave",
            "public and government holiday", "public holiday", "government holiday",
            "optional leave", "rest and recreation leave", "special disability leave",
            "special sick leave", "leave of vacation department", "departmental leave",
            "hospital leave", "compulsory leave", "leave without pay", "quarantine leave",
            "maternity leave"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);

        textViewPosts = findViewById(R.id.textViewPosts);
        db = FirebaseFirestore.getInstance();
        userId = getIntent().getStringExtra("userId");

        tvR = findViewById(R.id.tvR);
        tvPython = findViewById(R.id.tvPython);
        tvCPP = findViewById(R.id.tvCPP);
        tvLeave = findViewById(R.id.tvLeave);
        pieChart = findViewById(R.id.piechart);

        loadUserPosts();
    }

    private void loadUserPosts() {
        db.collection("Attendance")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Post> postsList = new ArrayList<>();
                            Map<String, Map<String, Integer>> monthlyLeaveTypeDays = new HashMap<>();
                            Map<String, Integer> yearlyLeaveTypeDays = new HashMap<>();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String attendanceType = document.getString("attendanceType");
                                long postingTime = document.getLong("postingTime");
                                String numberOfDays = document.getString("numberOfDays");
                                Date date = new Date(postingTime);

                                postsList.add(new Post(date, attendanceType, numberOfDays));

                                if (leaveTypes.contains(attendanceType.toLowerCase())) {
                                    String month = monthFormat.format(date);

                                    if (!monthlyLeaveTypeDays.containsKey(month)) {
                                        monthlyLeaveTypeDays.put(month, new HashMap<>());
                                    }

                                    Map<String, Integer> leaveTypeDays = monthlyLeaveTypeDays.get(month);
                                    int days = 0;
                                    if (numberOfDays != null && !numberOfDays.isEmpty()) {
                                        try {
                                            days = Integer.parseInt(numberOfDays);
                                        } catch (NumberFormatException e) {
                                            days = 0;
                                        }
                                    }

                                    leaveCount += days;
                                    if (!leaveTypeDays.containsKey(attendanceType)) {
                                        leaveTypeDays.put(attendanceType, days);
                                    } else {
                                        leaveTypeDays.put(attendanceType, leaveTypeDays.get(attendanceType) + days);
                                    }

                                    if (!yearlyLeaveTypeDays.containsKey(attendanceType)) {
                                        yearlyLeaveTypeDays.put(attendanceType, days);
                                    } else {
                                        yearlyLeaveTypeDays.put(attendanceType, yearlyLeaveTypeDays.get(attendanceType) + days);
                                    }
                                }

                                if ("Intime".equals(attendanceType)) {
                                    intimeCount++;
                                } else if ("Late".equals(attendanceType)) {
                                    lateCount++;
                                }

                                totalCount++;
                            }

                            Collections.sort(postsList, new Comparator<Post>() {
                                @Override
                                public int compare(Post o1, Post o2) {
                                    return o1.getDate().compareTo(o2.getDate());
                                }
                            });

                            StringBuilder posts = new StringBuilder();
                            for (Post post : postsList) {
                                String dateStr = sdf.format(post.getDate());
                                posts.append("Date: ").append(dateStr).append("\n")
                                        .append("Type: ").append(post.getAttendanceType());

                                if (leaveTypes.contains(post.getAttendanceType().toLowerCase()) && post.getNumberOfDays() != null) {
                                    posts.append(" (").append(post.getNumberOfDays()).append(" days leave)");
                                }

                                posts.append("\n\n");
                            }

                            textViewPosts.setText(posts.toString().trim());

                            StringBuilder monthlyLeaveReport = new StringBuilder();
                            monthlyLeaveReport.append("Leave days by month and attendance type:\n");
                            for (Map.Entry<String, Map<String, Integer>> monthEntry : monthlyLeaveTypeDays.entrySet()) {
                                String month = monthEntry.getKey();
                                monthlyLeaveReport.append("Month: ").append(month).append("\n");

                                Map<String, Integer> leaveTypeDays = monthEntry.getValue();
                                for (Map.Entry<String, Integer> typeEntry : leaveTypeDays.entrySet()) {
                                    monthlyLeaveReport.append("  Type: ").append(typeEntry.getKey())
                                            .append(" - Total Days: ").append(typeEntry.getValue()).append("\n");
                                }
                            }

                            textViewPosts.append("\n\n" + monthlyLeaveReport.toString().trim());

                            StringBuilder yearlyLeaveReport = new StringBuilder();
                            yearlyLeaveReport.append("Total leave days by attendance type for the year:\n");
                            for (Map.Entry<String, Integer> entry : yearlyLeaveTypeDays.entrySet()) {
                                yearlyLeaveReport.append("Type: ").append(entry.getKey())
                                        .append(" - Total Days: ").append(entry.getValue()).append("\n");
                            }

                            textViewPosts.append("\n\n" + yearlyLeaveReport.toString().trim());

                            storeLeaveDaysInFirestore(yearlyLeaveTypeDays);
                            setData();
                        } else {
                            textViewPosts.setText("Error: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void setData() {
        int adjustedTotalCount = totalCount + leaveCount;

        if (adjustedTotalCount == 0) {
            tvR.setText("0%");
            tvPython.setText("0%");
            tvLeave.setText("0%");
            tvCPP.setText("100%");

            pieChart.clearChart();
            pieChart.addPieSlice(new PieModel("Absent", 100, Color.parseColor("#EF5350")));
            pieChart.startAnimation();
            return;
        }

        double intimePercentage = (intimeCount / (double) adjustedTotalCount) * 100;
        double latePercentage = (lateCount / (double) adjustedTotalCount) * 100;
        double leavePercentage = (leaveCount / (double) adjustedTotalCount) * 100;
        double absentPercentage = 100 - (intimePercentage + latePercentage + leavePercentage);

        if (absentPercentage < 0) {
            absentPercentage = 0;
        }

        tvR.setText(String.format(Locale.getDefault(), "%.2f%%", latePercentage));
        tvPython.setText(String.format(Locale.getDefault(), "%.2f%%", intimePercentage));
        tvLeave.setText(String.format(Locale.getDefault(), "%.2f%%", leavePercentage));
        tvCPP.setText(String.format(Locale.getDefault(), "%.2f%%", absentPercentage));


        pieChart.clearChart();
        pieChart.addPieSlice(new PieModel("Late", (float) latePercentage, Color.parseColor("#FFA726")));
        pieChart.addPieSlice(new PieModel("Intime", (float) intimePercentage, Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(new PieModel("Absent", (float) absentPercentage, Color.parseColor("#EF5350")));
        pieChart.addPieSlice(new PieModel("Leave", (float) leavePercentage, Color.parseColor("#29B6F6")));

        pieChart.startAnimation();
    }

    private void storeLeaveDaysInFirestore(Map<String, Integer> yearlyLeaveTypeDays) {
        for (Map.Entry<String, Integer> entry : yearlyLeaveTypeDays.entrySet()) {
            String attendanceType = entry.getKey();
            int totalDays = entry.getValue();

            String documentId = userId + "_" + attendanceType;

            Map<String, Object> leaveData = new HashMap<>();
            leaveData.put("userId", userId);
            leaveData.put("attendanceType", attendanceType);
            leaveData.put("totalDays", totalDays);

            db.collection("UserLeaveRecords").document(documentId)
                    .set(leaveData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Leave record successfully written for: " + attendanceType);
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error writing leave record", e);
                    });
        }
    }

    private static class Post {
        private Date date;
        private String attendanceType;
        private String numberOfDays;

        public Post(Date date, String attendanceType, String numberOfDays) {
            this.date = date;
            this.attendanceType = attendanceType;
            this.numberOfDays = numberOfDays;
        }

        public Date getDate() {
            return date;
        }

        public String getAttendanceType() {
            return attendanceType;
        }

        public String getNumberOfDays() {
            return numberOfDays;
        }
    }

}
