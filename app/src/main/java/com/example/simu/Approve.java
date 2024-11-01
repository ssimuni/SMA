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
import com.google.firebase.auth.FirebaseAuth;
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
    private String currentUserDesignation;
    private String currentUserDepartment;

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

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            currentUserDesignation = documentSnapshot.getString("designation");
            currentUserDepartment = documentSnapshot.getString("department");

            loadPendingUsers();
        });
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
                                String pendingUserDesignation = document.getString("designation");
                                String pendingUserDepartment = document.getString("department");
                                String pendingUserOffice = document.getString("office");

                                if (!Objects.equals(pendingUserDepartment, currentUserDepartment)) {
                                    continue;
                                }
                                switch (currentUserDesignation) {
                                    case "Upazila level Officer":
                                        assert pendingUserDesignation != null;
                                        if (isUpazilaLevelUserValid(pendingUserDesignation, pendingUserOffice)) {
                                            addUserToList(document);
                                        }
                                        break;
                                    case "District level Officer":
                                        assert pendingUserDesignation != null;
                                        if (isDistrictLevelUserValid(pendingUserDesignation, pendingUserOffice)) {
                                            addUserToList(document);
                                        }
                                        break;
                                    case "Division level Officer":
                                        assert pendingUserDesignation != null;
                                        if (isDivisionLevelUserValid(pendingUserDesignation, pendingUserOffice)) {
                                            addUserToList(document);
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                            userAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(Approve.this, "Error fetching users: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isUpazilaLevelUserValid(String designation, String office) {
        return (designation.equals("Union level Officer") || designation.equals("Upazila level Officer") ||
                designation.equals("Union level Worker") || designation.equals("Upazila level Worker")) &&
                (office != null && office.contains("Upazila"));
    }

    private boolean isDistrictLevelUserValid(String designation, String office) {
        return (designation.equals("Upazila level Officer") || designation.equals("District level Officer") ||
                designation.equals("District level Worker")) &&
                (office != null && (office.contains("Upazila") || office.contains("District")));
    }

    private boolean isDivisionLevelUserValid(String designation, String office) {
        return (designation.equals("District level Officer") || designation.equals("Division level Officer") ||
                designation.equals("Division level Worker")) &&
                (office != null && (office.contains("District") || office.contains("Division")));
    }


    private void addUserToList(DocumentSnapshot document) {
        String userId = document.getId();
        String name = document.getString("name");
        String designation = document.getString("designation");
        String department = document.getString("department");
        String workstation = document.getString("workstation");
        String directorate = document.getString("directorate");
        String designationType = document.getString("designation_type");
        userList.add(new User(userId, name, designation, department, workstation, directorate, designationType));    }

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
            holder.textViewDepartment.setText(user.getDepartment());
            holder.textViewWorkstation.setText(user.getWorkstation());
            holder.textViewDirectorate.setText(user.getDirectorate());
            String designationWithType = user.getDesignation() + " (" + user.getDesignationType() + ")";
            holder.textViewDesignation.setText(designationWithType);

            holder.buttonApprove.setOnClickListener(v -> holder.approveUser(user.getUserId()));

            holder.buttonReject.setOnClickListener(v -> holder.rejectUser(user.getUserId(), UserAdapter.this));
        }


        @Override
        public int getItemCount() {
            return userList.size();
        }

        public static class UserViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName, textViewDesignation, textViewDepartment, textViewWorkstation, textViewDirectorate;
            Button buttonApprove, buttonReject;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textViewName);
                textViewDesignation = itemView.findViewById(R.id.textViewDesignation);
                textViewDepartment = itemView.findViewById(R.id.textViewDepartment);
                textViewWorkstation = itemView.findViewById(R.id.textViewWorkstation);
                textViewDirectorate = itemView.findViewById(R.id.textViewDirectorate);
                buttonApprove = itemView.findViewById(R.id.buttonApprove);
                buttonReject = itemView.findViewById(R.id.buttonReject);
            }

            public void approveUser(String userId) {
                FirebaseFirestore.getInstance().collection("users")
                        .document(userId)
                        .update("isApproved", "Yes")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(itemView.getContext(), "User approved successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(itemView.getContext(), "Error approving user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            public void rejectUser(String userId, UserAdapter adapter) {
                FirebaseFirestore.getInstance().collection("users")
                        .document(userId)
                        .update("isApproved", "out")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                int position = getAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    adapter.userList.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    Toast.makeText(itemView.getContext(), "User rejected successfully", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(itemView.getContext(), "Error rejecting user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
        private String designationType;
        public User(String userId, String name, String designation, String department, String workstation, String directorate, String designationType) {
            this.userId = userId;
            this.name = name;
            this.designation = designation;
            this.department = department;
            this.workstation = workstation;
            this.directorate = directorate;
            this.designationType = designationType;
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

        public String getDirectorate() {
            return directorate;
        }
        public String getDesignationType() {
            return designationType;
        }
    }
}
