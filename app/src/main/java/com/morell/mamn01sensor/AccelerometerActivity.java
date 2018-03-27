package com.morell.mamn01sensor;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.morell.mamn01sensor.databinding.ActivityAccelerometerBinding;

import java.text.DecimalFormat;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {
    private ActivityAccelerometerBinding binding;
    private float[] filteredValues;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private DecimalFormat numberFormatter = new DecimalFormat("#.#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_accelerometer);

        try {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        catch (Exception e) {
            //This snippet is inspired by https://developer.android.com/guide/topics/ui/notifiers/toasts.html
            Context context = getApplicationContext();
            CharSequence text = "Could not connect to accelerometer";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(filteredValues == null)
            filteredValues = sensorEvent.values;
        runLowPassFilter(sensorEvent.values);
        binding.setX(round(filteredValues[0], 1));
        binding.setY(round(filteredValues[1], 1));
        binding.setZ(round(filteredValues[2], 1));
    }

    //https://stackoverflow.com/questions/22186778/using-math-round-to-round-to-one-decimal-place
    private static float round (float value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (float) Math.round(value * scale) / scale;
    }

    private void runLowPassFilter(float[] sensorReadings) {
        //This code snippet is borrowed from https://developer.android.com/reference/android/hardware/SensorEvent.html
        for (int i = 0; i < sensorReadings.length; i++) {
            final float FILTER_ALPHA = 0.8f;
            filteredValues[i] = FILTER_ALPHA * filteredValues[i] + (1 - FILTER_ALPHA) * sensorReadings[i];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
