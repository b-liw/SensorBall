package com.tutorial.sensorball.sensorball;


public class PlayerBall implements Ball {

    private final float DEFAULT_RADIUS = 25.0f;
    private final float DEFAULT_SPEED = 3.0f;

    private float x;
    private float y;
    private float xv;
    private float yv;
    private float xa;
    private float ya;
    private float speed;
    private float maxX;
    private float maxY;
    private float radius;

    public PlayerBall(int x, int y, int maxX, int maxY) {
        this.x = x;
        this.y = y;
        this.speed = DEFAULT_SPEED;
        this.maxX = maxX;
        this.maxY = maxY;
        this.radius = DEFAULT_RADIUS;
    }

    public void update() {
        xv += xa * speed;
        yv += ya * speed;
        float xS = (xv / 2);
        float yS = (yv / 2);

        x += xS;
        y -= yS;

        if (x + radius > maxX) {
            x = maxX - radius;
            xa *= -1;
            xv /= 10;
        } else if (x < radius) {
            x = radius;
            xa *= -1;
            xv /= 10;
        }

        if (y + radius > maxY) {
            y = maxY - radius;
            ya *= -1;
            yv /= 10;
        } else if (y < radius) {
            y = radius;
            ya *= -1;
            yv /= 10;
        }
    }

    public float getSpeed() {
        return speed;
    }

    public float getRadius() {
        return radius;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setXa(float xa) {
        this.xa = xa;
    }

    public void setYa(float ya) {
        this.ya = ya;
    }
}
