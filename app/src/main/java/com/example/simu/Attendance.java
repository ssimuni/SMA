package com.example.simu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.util.Calendar;

public class Attendance extends AppCompatActivity {

    Button activites_feed, intime, late, approved_leave, training, exit1, exit2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        activites_feed = findViewById(R.id.activites_feed);
        intime = findViewById(R.id.entry);
        late = findViewById(R.id.late);
        approved_leave = findViewById(R.id.approved_leave);
        training = findViewById(R.id.training);
        exit1 = findViewById(R.id.exit1);
        exit2 = findViewById(R.id.exit2);

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
                checkLateValidity();
            }
        });

        exit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkExit1Validity();
            }
        });

        exit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkExit2Validity();
            }
        });

        approved_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("approved_leave");
            }
        });

        training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithAttendanceType("training");
            }
        });
    }
    private void checkIntimeValidity() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NTPUDPClient client = new NTPUDPClient();
                    InetAddress inetAddress = InetAddress.getByName("pool.ntp.org");
                    TimeInfo timeInfo = client.getTime(inetAddress);
                    long currentTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentTime);

                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    if ((hour == 8 && minute >= 45) || (hour == 9 && minute <= 15)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                intime.setEnabled(true);
                                startActivityWithAttendanceType("intime");
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                intime.setEnabled(false);
                                Toast.makeText(Attendance.this, "You are late!!!", Toast.LENGTH_SHORT).show();
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

    private void checkLateValidity() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NTPUDPClient client = new NTPUDPClient();
                    InetAddress inetAddress = InetAddress.getByName("pool.ntp.org");
                    TimeInfo timeInfo = client.getTime(inetAddress);
                    long currentTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentTime);

                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    if ((hour == 9 && minute >= 16) || (hour == 10 && minute == 0)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                late.setEnabled(true);
                                startActivityWithAttendanceType("late");
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                late.setEnabled(false);
                                Toast.makeText(Attendance.this, "Time is over!!!", Toast.LENGTH_SHORT).show();
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

    private void checkExit1Validity() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NTPUDPClient client = new NTPUDPClient();
                    InetAddress inetAddress = InetAddress.getByName("pool.ntp.org");
                    TimeInfo timeInfo = client.getTime(inetAddress);
                    long currentTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentTime);

                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    if (hour == 15 && minute <= 30) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                exit1.setEnabled(true);
                                startActivityWithAttendanceType("exit1");
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                exit1.setEnabled(false);
                                Toast.makeText(Attendance.this, "Time is over!!!", Toast.LENGTH_SHORT).show();
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

    private void checkExit2Validity() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NTPUDPClient client = new NTPUDPClient();
                    InetAddress inetAddress = InetAddress.getByName("pool.ntp.org");
                    TimeInfo timeInfo = client.getTime(inetAddress);
                    long currentTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentTime);

                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    if (hour > 17 || (hour == 17 && minute > 0)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                exit2.setEnabled(true);
                                startActivityWithAttendanceType("exit2");
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                exit2.setEnabled(false);
                                Toast.makeText(Attendance.this, "Click after 5 pm.", Toast.LENGTH_SHORT).show();
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
    private void startActivityWithAttendanceType(String attendanceType) {
        Intent intent = new Intent(Attendance.this, Upload_attendance.class);
        intent.putExtra("attendanceType", attendanceType);
        startActivity(intent);
    }
}