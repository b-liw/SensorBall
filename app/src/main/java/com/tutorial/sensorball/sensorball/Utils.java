package com.tutorial.sensorball.sensorball;

public class Utils {
    public static int rand(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }
}
