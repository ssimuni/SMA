package com.example.simu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.util.Calendar;

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
                checkIntimeValidity();
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
    private void checkIntimeValidity() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Fetch current time from NTP server
                    NTPUDPClient client = new NTPUDPClient();
                    InetAddress inetAddress = InetAddress.getByName("pool.ntp.org");
                    TimeInfo timeInfo = client.getTime(inetAddress);
                    long currentTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

                    // Convert current time to Calendar instance
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentTime);

                    // Check if current time is within the allowed time range (8:45 am to 9:15 am)
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    if ((hour == 8 && minute >= 45) || (hour == 9 && minute <= 15)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Enable the intime button
                                intime.setEnabled(true);
                                startActivity(new Intent(Attendance.this, Upload_attendance.class));
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Disable the intime button
                                intime.setEnabled(false);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}