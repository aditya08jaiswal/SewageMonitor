package com.vysiontech.sewagemonitor;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Open Splash Activity
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

}