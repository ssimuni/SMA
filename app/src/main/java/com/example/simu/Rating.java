package com.example.simu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class Rating extends AppCompatActivity {

    RatingBar mRating;
    Button mSubmit;
    TextView mThank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        mRating = findViewById(R.id.rating);
        mSubmit = findViewById(R.id.submit);
        mThank = findViewById(R.id.thank);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = mRating.getRating();

                mThank.setVisibility(View.VISIBLE);
                mSubmit.setVisibility(View.INVISIBLE);

                if (rating == 5) {
                    mThank.setText(R.string.thank_you);
                } else if (rating == 0) {
                    mThank.setText(R.string.very_disappointing);
                } else {
                    mThank.setText(R.string.thank_you_for_your_feedback);
                }
            }
        });
}
}