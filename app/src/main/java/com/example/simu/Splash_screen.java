package com.example.simu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Splash_screen extends AppCompatActivity {

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fAuth = FirebaseAuth.getInstance();
        ImageView logoImageView = findViewById(R.id.logo);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserAuthentication();
            }
        }, 2000);

        logoImageView.setAlpha(0f);
        logoImageView.animate().alpha(1f).setDuration(2000).withEndAction(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {}
                }, 3000);
            }
        });
    }

    private void checkUserAuthentication() {

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", true);

        FirebaseUser user = fAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getApplicationContext(), Register.class));
        } else if (!user.isEmailVerified()) {
            startActivity(new Intent(getApplicationContext(), Register.class));
        } else if (!isLoggedIn) {
            startActivity(new Intent(getApplicationContext(), Login.class));
        } else {
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
        }
        finish();
    }
}