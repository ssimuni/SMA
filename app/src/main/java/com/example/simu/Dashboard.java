package com.example.simu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Dashboard extends AppCompatActivity {

    private CardView profileCard;
    private CardView aboutCard;
    private CardView rateCard;
    private CardView newsFeed;
    private CardView location;
    private CardView weather;
    private CardView attendance;
    private CardView country;
    private CardView blood;
    private CardView deptNews;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        profileCard = findViewById(R.id.level1).findViewById(R.id.profile);
        aboutCard = findViewById(R.id.level1).findViewById(R.id.about);
        rateCard = findViewById(R.id.level4).findViewById(R.id.rating);
        newsFeed = findViewById(R.id.level2).findViewById(R.id.feed);
        location = findViewById(R.id.level3).findViewById(R.id.location);
        weather = findViewById(R.id.level3).findViewById(R.id.weather);
        attendance = findViewById(R.id.level5).findViewById(R.id.attendance);
        country = findViewById(R.id.level4).findViewById(R.id.country);
        blood = findViewById(R.id.level5).findViewById(R.id.blood);
        deptNews = findViewById(R.id.level2).findViewById(R.id.deptNews);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

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

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(Dashboard.this, MapsActivity.class));}
        });

        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(Dashboard.this, WeatherActivity.class));}
        });

        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(Dashboard.this, Attendance.class));}
        });

        deptNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(Dashboard.this, Dept_NewsFeed.class));}
        });

        blood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(Dashboard.this, Blood.class));}
        });

        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(Dashboard.this, CountryActivity.class));}
        });
    }
}