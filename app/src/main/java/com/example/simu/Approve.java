package com.example.simu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Approve extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance();
        loadPendingUsers();
    }

    private void loadPendingUsers() {
        db.collection("users")
                .whereEqualTo("isApproved", "No")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                String userId = document.getId();
                                String name = document.getString("name");
                                String designation = document.getString("designation");
                                String department = document.getString("department");
                                String workstation = document.getString("workstation");
                                String directorate = document.getString("directorate");
                                userList.add(new User(userId, name, designation, department, workstation, directorate));
                            }
                            userAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(Approve.this, "Error fetching users: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private static class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

        private List<User> userList;

        public UserAdapter(List<User> userList) {
            this.userList = userList;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_approve, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = userList.get(position);
            holder.textViewName.setText(user.getName());
            holder.textViewDesignation.setText(user.getDesignation());
            holder.textViewDepartment.setText(user.getDepartment());
            holder.textViewWorkstation.setText(user.getWorkstation());
            holder.textViewDirectorate.setText(user.getDirectorate());

            holder.buttonApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.approveUser(user.getUserId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public static class UserViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName, textViewDesignation, textViewDepartment, textViewWorkstation, textViewDirectorate;
            Button buttonApprove;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textViewName);
                textViewDesignation = itemView.findViewById(R.id.textViewDesignation);
                textViewDepartment = itemView.findViewById(R.id.textViewDepartment);
                textViewWorkstation = itemView.findViewById(R.id.textViewWorkstation);
                textViewDirectorate = itemView.findViewById(R.id.textViewDirectorate);
                buttonApprove = itemView.findViewById(R.id.buttonApprove);
            }


            public void approveUser(String userId) {

                FirebaseFirestore.getInstance().collection("users")
                        .document(userId)
                        .update("isApproved", "Yes")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(itemView.getContext(), "User approved successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(itemView.getContext(), "Error approving user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    private static class User {
        private String userId;
        private String name;
        private String designation;
        private String department;
        private String workstation;
        private String directorate;

        public User(String userId, String name, String designation, String department, String workstation, String directorate) {
            this.userId = userId;
            this.name = name;
            this.designation = designation;
            this.department = department;
            this.workstation = workstation;
            this.directorate = directorate;
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

        public String getDepartment() {
            return department;
        }

        public String getWorkstation() {
            return workstation;
        }

        public String getDirectorate(){return directorate;}
    }
}
