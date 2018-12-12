package com.example.g315.rgb2gray.class_1116;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.g315.rgb2gray.R;

public class HomeActivity extends AppCompatActivity {
    public static final int MEAN_BLUR = 1;
    public static final int MEDIAN_BLUR = 2;
    public static final int GAUSSIAN_BLUR = 3;
    public static final int SHARPEN = 4;
    public static final int THRESHOLD = 5;
    public static final int ADAPTIVE_THRESHOLD = 6;
    public static final int OTSU_THRESHOLD = 7;
    public static final int REGION_LABELING = 8;
    public static final int DILATE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.bMean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MeanBlur1116Activity.class);
                i.putExtra("ACTION_MODE", MEAN_BLUR);
                startActivity(i);
            }
        });

        findViewById(R.id.bGaussian).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MeanBlur1116Activity.class);
                i.putExtra("ACTION_MODE", GAUSSIAN_BLUR);
                startActivity(i);
            }
        });

        findViewById(R.id.bMedian).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MeanBlur1116Activity.class);
                i.putExtra("ACTION_MODE", MEDIAN_BLUR);
                startActivity(i);
            }
        });

        findViewById(R.id.bSharpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MeanBlur1116Activity.class);
                i.putExtra("ACTION_MODE", SHARPEN);
                startActivity(i);
            }
        });

        findViewById(R.id.bThreshold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MeanBlur1116Activity.class);
                i.putExtra("ACTION_MODE", THRESHOLD);
                startActivity(i);
            }
        });

        //1207
        findViewById(R.id.bAdaptive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MeanBlur1116Activity.class);
                i.putExtra("ACTION_MODE", ADAPTIVE_THRESHOLD);
                startActivity(i);
            }
        });


        findViewById(R.id.bOtsu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MeanBlur1116Activity.class);
                i.putExtra("ACTION_MODE", OTSU_THRESHOLD);
                startActivity(i);
            }
        });

        findViewById(R.id.bRegionLabeling).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MeanBlur1116Activity.class);
                i.putExtra("ACTION_MODE", REGION_LABELING);
                startActivity(i);
            }
        });

    }
}
