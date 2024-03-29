package com.example.simu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Attendance extends AppCompatActivity {

    Button activites_feed, intime, late, approved_leave, training, exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        activites_feed = findViewById(R.id.activites_feed);
        intime = findViewById(R.id.entry);
        late = findViewById(R.id.late);
        approved_leave = findViewById(R.id.approved_leave);
        training = findViewById(R.id.training);
        exit = findViewById(R.id.exit);

        activites_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Attendance.this, ActivitiesFeed.class));
            }
        });

        intime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Attendance.this, Upload_attendance.class));
            }
        });

        late.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Attendance.this, Upload_attendance.class));
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Attendance.this, Upload_attendance.class));
            }
        });

        approved_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Attendance.this, Upload_attendance.class));
            }
        });

        training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Attendance.this, Upload_attendance.class));
            }
        });
    }
}