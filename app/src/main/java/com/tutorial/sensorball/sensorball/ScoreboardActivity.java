package com.tutorial.sensorball.sensorball;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ScoreboardActivity extends AppCompatActivity {

    private static final String PREFERENCES_NAME = "SENSOR_BALL_DATA";
    private static final String PREFERENCES_BEST_SCORES = "PREFERENCES_BEST_SCORES";

    private ListView scoreboardList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        createBestScoresList();
    }

    private void createBestScoresList() {
        scoreboardList = findViewById(R.id.scoreBoardListView);
        List<String> bestScoresStrings = getBestScores();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bestScoresStrings);
        if (adapter.getCount() == 0) {
            Toast.makeText(this, "No items available", Toast.LENGTH_SHORT).show();
        }
        scoreboardList.setAdapter(adapter);
    }

    @NonNull
    private List<String> getBestScores() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Set<String> bestScores = sharedPreferences.getStringSet(PREFERENCES_BEST_SCORES, new HashSet<String>());
        List<ScoreWithDate> bestScoresWithDate = new ArrayList<>();
        List<String> bestScoresStrings = new ArrayList<>();
        Iterator<String> scoreIterator = bestScores.iterator();
        while (scoreIterator.hasNext()) {
            String score = scoreIterator.next();
            String[] splitted = score.split(",");
            bestScoresWithDate.add(new ScoreWithDate(splitted[0], splitted[1]));
        }

        Collections.sort(bestScoresWithDate, new Comparator<ScoreWithDate>() {
            @Override
            public int compare(ScoreWithDate o1, ScoreWithDate o2) {
                return Integer.valueOf(o2.getScore()).compareTo(Integer.valueOf(o1.getScore()));
            }
        });
        for (ScoreWithDate score : bestScoresWithDate) {
            bestScoresStrings.add(score.toString());
        }
        return bestScoresStrings;
    }
}
