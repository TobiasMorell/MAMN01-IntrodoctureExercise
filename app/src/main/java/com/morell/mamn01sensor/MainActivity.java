package com.morell.mamn01sensor;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.morell.mamn01sensor.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    public void openCompass(View view) {
        Intent compassIntent = new Intent(this, CompasActivity.class);
        startActivity(compassIntent);
    }

    public void openAccelerometer(View view) {
        Intent accelerometerIntent = new Intent (this, AccelerometerActivity.class);
        startActivity(accelerometerIntent);
    }
}
