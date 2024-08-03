package com.example.simu;

import android.graphics.Color;
import android.os.Bundle;
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
import java.util.List;
import java.util.Locale;

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
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String attendanceType = document.getString("attendanceType");
                                long postingTime = document.getLong("postingTime");
                                Date date = new Date(postingTime);

                                postsList.add(new Post(date, attendanceType));

                                if ("Intime".equals(attendanceType)) {
                                    intimeCount++;
                                } else if ("Late".equals(attendanceType)) {
                                    lateCount++;
                                } else if (leaveTypes.contains(attendanceType.toLowerCase())) {
                                    leaveCount++;
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
                                        .append("Type: ").append(post.getAttendanceType()).append("\n\n");
                            }

                            textViewPosts.setText(posts.toString().trim());
                            setData();
                        } else {
                            textViewPosts.setText("Error: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void setData() {
        double intimePercentage = (intimeCount / (double) totalCount) * 100;
        double latePercentage = (lateCount / (double) totalCount) * 100;
        double leavePercentage = (leaveCount / (double) totalCount) * 100;
        double absentPercentage = 100 - (intimePercentage + latePercentage + leavePercentage);

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

    private static class Post {
        private Date date;
        private String attendanceType;

        public Post(Date date, String attendanceType) {
            this.date = date;
            this.attendanceType = attendanceType;
        }

        public Date getDate() {
            return date;
        }

        public String getAttendanceType() {
            return attendanceType;
        }
    }
}
