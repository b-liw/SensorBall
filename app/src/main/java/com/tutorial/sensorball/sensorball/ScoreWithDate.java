package com.tutorial.sensorball.sensorball;

public class ScoreWithDate {
    private String score;
    private String date;

    public ScoreWithDate(String score, String date) {
        this.score = score;
        this.date = date;
    }

    @Override
    public String toString() {
        return score + ", " + date;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
