package com.tutorial.sensorball.sensorball;

public class ScorePoint implements Ball {

    private final float DEFAULT_RADIUS = 20.0f;

    private float x;
    private float y;
    private float radius;
    private boolean active;

    public ScorePoint() {
        this(0, 0);
    }

    public ScorePoint(float x, float y) {
        this.x = x;
        this.y = y;
        this.radius = DEFAULT_RADIUS;
        active = false;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
