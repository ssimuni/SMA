package com.example.simu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Splash_screen extends AppCompatActivity {

    private static final String HTML_FILE = "file:///android_asset/splash_animation.html";
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fAuth = FirebaseAuth.getInstance();

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl(HTML_FILE);


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserAuthentication();
            }
        }, 3000);

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