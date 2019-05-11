package com.tutorial.sensorball.sensorball;

import android.graphics.Path;

public class SierpinskiTriangle {
    private int id;
    private static int nextId = 0;
    private final Path path;
    private final int depth;
    private final float x;
    private final float y;
    private final float size;

    public SierpinskiTriangle(int depth, float x, float y, float size) {
        this.id = nextId++;
        this.depth = depth;
        this.x = x;
        this.y = y;
        this.size = size;
        this.path = createPath();
    }

    private Path createPath() {
        Path path = new Path();
        sierpinski(path, depth, x, y, size);
        path.close();
        return path;
    }

    public void sierpinski(Path path, int n, float x, float y, float size) {
        if (n == 0) {
            return;
        }

        float x0 = x;
        float y0 = y;
        float x1 = x0 + size;
        float y1 = y0;
        float x2 = x0 + size / 2;
        float y2 = (float) (y0 + (Math.sqrt(3)) * size / 2);

        path.moveTo(x0, y0);
        path.lineTo(x1, y1);
        path.lineTo(x0, y0);
        path.lineTo(x2, y2);
        path.lineTo(x0, y0);
        path.lineTo(x1, y1);
        path.lineTo(x2, y2);

        sierpinski(path, n-1, x0, y0, size / 2);
        sierpinski(path, n-1, (x0 + x1) / 2, (y0 + y1) / 2, size / 2);
        sierpinski(path, n-1, (x0 + x2) / 2, (y0 + y2) / 2, size / 2);
    }

    public Path getPath() {
        return path;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SierpinskiTriangle that = (SierpinskiTriangle) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
