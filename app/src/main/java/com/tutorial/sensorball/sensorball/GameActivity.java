package com.tutorial.sensorball.sensorball;

import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        gameView = new GameView(this, point.x, point.y);
        setContentView(gameView);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
        sensorManager.unregisterListener(gameView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(gameView, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(gameView, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        gameView.resume();
    }

}
