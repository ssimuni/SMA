package com.example.simu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MonthlyReport extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserAttendance> userAttendanceList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAttendanceList = new ArrayList<>();
        userAdapter = new UserAdapter(userAttendanceList);
        recyclerView.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance();
        loadUsersAndAttendanceFromFirestore();
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
                            }
                            loadAttendanceFromFirestore(userMap);
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
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = document.getString("userId");
                                String attendanceType = document.getString("attendanceType");
                                long postingTime = document.getLong("postingTime");

                                User user = userMap.get(userId);
                                if (user != null) {
                                    userAttendanceList.add(new UserAttendance(user, new Attendance(attendanceType, postingTime)));
                                }
                            }
                            userAdapter.notifyDataSetChanged();
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

    private static class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

        private List<UserAttendance> userAttendanceList;

        public UserAdapter(List<UserAttendance> userAttendanceList) {
            this.userAttendanceList = userAttendanceList;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.monthly_report_table, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            UserAttendance userAttendance = userAttendanceList.get(position);
            User user = userAttendance.getUser();
            Attendance attendance = userAttendance.getAttendance();

            holder.textViewName.setText(user.getName());
            holder.textViewDesignation.setText(user.getDesignation());
            holder.textViewWorkstation.setText(user.getWorkstation());
            holder.date.setText(formatDate(attendance.getPostingTime()));

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

        @Override
        public int getItemCount() {
            return userAttendanceList.size();
        }

        public static class UserViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName, textViewDesignation, textViewWorkstation, date, intime, late, exit, approved_leave, training, urgent;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textViewName);
                textViewDesignation = itemView.findViewById(R.id.textViewDesignation);
                textViewWorkstation = itemView.findViewById(R.id.textViewWorkstation);
                date = itemView.findViewById(R.id.date);
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

    private static class UserAttendance {
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

    private static String formatDate(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date(timeInMillis);
        return sdf.format(date);
    }

    private static String formatTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date(timeInMillis);
        return sdf.format(date);
    }
}
