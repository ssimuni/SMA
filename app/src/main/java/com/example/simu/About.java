package com.example.simu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class About extends AppCompatActivity {
    TextView textViewWebsite;
    TextView textViewYouTube;
    TextView textViewFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        textViewWebsite = findViewById(R.id.web_link);
        textViewYouTube = findViewById(R.id.youtube_link);
        textViewFacebook = findViewById(R.id.facebook_link);
        textViewWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("http://www.google.com");
            }
        });

        textViewYouTube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.youtube.com/watch?v=ZYvbmVEwX14&list=PLgH5QX0i9K3p9xzYLFGdfYliIRBLVDRV5");
            }
        });

        textViewFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.facebook.com");
            }
        });
    }

    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}