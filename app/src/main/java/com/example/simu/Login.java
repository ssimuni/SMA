//package com.example.simu;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//
//import java.util.Objects;
//
//public class Login extends AppCompatActivity {
//
//    EditText mEmail, mPassword;
//    Button mButton;
//    TextView mRegister;
//    FirebaseAuth fAuth;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        mPassword = findViewById(R.id.password);
//        mEmail = findViewById(R.id.email);
//        mButton = findViewById(R.id.button);
//        mRegister = findViewById(R.id.newaccount);
//
//        fAuth = FirebaseAuth.getInstance();
//        if (isLoggedIn()) {
//            startActivity(new Intent(getApplicationContext(), Dashboard.class));
//            finish(); // Prevent going back to the login screen using the back button
//        }
//
//        mRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), Register.class));
//            }
//        });
//
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = mEmail.getText().toString().trim();
//                String password = mPassword.getText().toString().trim();
//
//                if (TextUtils.isEmpty(email)) {
//                    Toast.makeText(Login.this, "Email is required", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (TextUtils.isEmpty(password)) {
//                    Toast.makeText(Login.this, "Password is required", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//                            // Save user's login state
//                            saveLoginState(true);
//
//                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(getApplicationContext(), Dashboard.class));
//                            finish(); // Prevent going back to the login screen using the back button
//                        }
//                        else {
//                            // Check if the exception is due to incorrect password
//                            if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
//                                Toast.makeText(Login.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(Login.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
//
//            }
//        });
//    }
//
//}
package com.example.simu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mButton;
    TextView mRegister;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPassword = findViewById(R.id.password);
        mEmail = findViewById(R.id.email);
        mButton = findViewById(R.id.button);
        mRegister = findViewById(R.id.newaccount);

        fAuth = FirebaseAuth.getInstance();

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Email is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Password is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final String userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                            FirebaseFirestore.getInstance().collection("users")
                                    .document(userId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    String isApproved = document.getString("isApproved");
                                                    if (isApproved != null && isApproved.equals("Yes")) {
                                                        saveLoginState(true);
                                                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Wait for admin approval", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "User document does not exist", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(Login.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }
        });
    }
    private void saveLoginState(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }
}
