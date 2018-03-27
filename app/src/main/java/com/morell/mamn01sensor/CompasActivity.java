package com.morell.mamn01sensor;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.morell.mamn01sensor.databinding.ActivityCompasBinding;

import java.util.HashMap;
import java.util.Iterator;

public class CompasActivity extends AppCompatActivity implements SensorEventListener {
    private ActivityCompasBinding binding;
    HashMap<Integer, Sensor> availableSensors = new HashMap<>();
    private SensorManager sensorManager;
    private float[] rMat = new float[9];
    private float[] orientation = new float[3];
    private float[] lastAccelerometer = new float[3], lastMagnet = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compas);
        binding.setHeading(0);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        getSensors();
    }

    private void getSensors() {
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        Sensor magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if(accel == null && rotation == null && magnetic == null) {
            showNoSensorsAlert();
        }

        if(accel != null)
            availableSensors.put(Sensor.TYPE_ACCELEROMETER, accel);
        if(rotation != null)
            availableSensors.put(Sensor.TYPE_ROTATION_VECTOR, rotation);
        if(magnetic != null)
            availableSensors.put(Sensor.TYPE_MAGNETIC_FIELD, magnetic);

        registerSensors();
    }

    private void registerSensors() {
        for(Object o : availableSensors.values()) {
            Sensor s = (Sensor) o;
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void showNoSensorsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device does not support compass")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        for(Object o : availableSensors.values()) {
            Sensor s = (Sensor) o;
            sensorManager.unregisterListener(this, s);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensors();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int azimuth;
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, sensorEvent.values);
            azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
            binding.setHeading(azimuth);
            binding.setCompassRotation(-azimuth);
            return;
        }
        else if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            System.arraycopy(sensorEvent.values, 0, lastAccelerometer, 0, sensorEvent.values.length);
        else if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            System.arraycopy(sensorEvent.values, 0, lastMagnet, 0, sensorEvent.values.length);

        if(lastAccelerometer != null && lastMagnet != null) {
            SensorManager.getRotationMatrix(rMat, null, lastAccelerometer, lastMagnet);
            SensorManager.getOrientation(rMat, orientation);
            azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }
        else
            return;

        binding.setHeading(azimuth);
        binding.setCompassRotation(-azimuth);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
