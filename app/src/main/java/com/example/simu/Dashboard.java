package com.example.simu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Dashboard extends AppCompatActivity {

    private CardView profileCard;
    private CardView aboutCard;
    private CardView rateCard;
    private CardView newsFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        profileCard = findViewById(R.id.level1).findViewById(R.id.profile);
        aboutCard = findViewById(R.id.level1).findViewById(R.id.about);
        rateCard = findViewById(R.id.level2).findViewById(R.id.rating);
        newsFeed = findViewById(R.id.level2).findViewById(R.id.feed);

        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, Profile.class));
            }
        });

        aboutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, About.class));
            }
        });

        rateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, Rating.class));
            }
        });

        newsFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(Dashboard.this, NewsFeed.class));}
        });
    }
}