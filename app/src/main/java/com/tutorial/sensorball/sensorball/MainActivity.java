package com.tutorial.sensorball.sensorball;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button startButton;
    private Button scoreboardButton;
    private Button exitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViewHandles();
        initializeViewListeners();
    }

    private void initializeViewListeners() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButtonClickListener(v);
            }
        });

        scoreboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreboardButtonClickListener(v);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitButtonClickListener(v);
            }
        });
    }

    private void exitButtonClickListener(View v) {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private void scoreboardButtonClickListener(View v) {
        Intent intent = new Intent(this, ScoreboardActivity.class);
        startActivity(intent);
    }

    private void startButtonClickListener(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void initializeViewHandles() {
        startButton = findViewById(R.id.startBtn);
        scoreboardButton = findViewById(R.id.scoreboardBtn);
        exitButton = findViewById(R.id.exitBtn);
    }
}
