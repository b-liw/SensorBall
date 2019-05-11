package com.tutorial.sensorball.sensorball;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.tutorial.sensorball.sensorball.Utils.rand;

public class GameView extends SurfaceView implements Runnable, SensorEventListener {

    private static final int NUMBER_OF_OBSTACLES = 6;
    private static final String PREFERENCES_NAME = "SENSOR_BALL_DATA";
    private static final String PREFERENCES_BEST_SCORES = "PREFERENCES_BEST_SCORES";

    private final Context context;
    private volatile boolean isPlaying = true;
    private Thread gameThread;
    private PlayerBall playerBall;
    private ScorePoint scorePoint;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private SharedPreferences prefs;
    private long FPS = 60;
    private long delayTime = 1000 / FPS;

    private int timeLeft;
    private Timer gameTimer;

    private int maxX;
    private int maxY;

    private boolean isMagnetometerSet;
    private boolean isAccelerometerSet;
    private float[] accelerometerValues = new float[3];
    private float[] magnetometerValues = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientationMatrix = new float[3];

    private int score;

    private List<SierpinskiTriangle> triangleList;

    public GameView(Context context, int maxX, int maxY) {
        super(context);
        this.context = context;
        timeLeft = 60;
        surfaceHolder = getHolder();
        triangleList = new ArrayList<>();
        paint = new Paint();
        this.maxX = maxX;
        this.maxY = maxY;
        playerBall = new PlayerBall(50, 50, this.maxX, this.maxY);
        prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        scorePoint = new ScorePoint();
        createNewScorePoint();
        gameTimer = new Timer();
    }

    @Override
    public void run() {
        while (isPlaying) {
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            update();
            render();
        }
    }

    private void render() {
        canvas = surfaceHolder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.argb(255, 0, 0, 0));
            this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(0xFF00FF00);
            canvas.drawCircle(playerBall.getX(), playerBall.getY(), playerBall.getRadius(), paint);
            paint.setColor(0xFF0000FF);
            canvas.drawCircle(scorePoint.getX(), scorePoint.getY(), scorePoint.getRadius(), paint);
            paint.setColor(0xFFFF0000);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(50);
            canvas.drawText("Score: " + score + ", time left: " + timeLeft,0, 50, paint);
            paint.setStrokeWidth(3);
            paint.setPathEffect(null);
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.STROKE);
            for (SierpinskiTriangle sierpinskiTriangle : triangleList) {
                canvas.drawPath(sierpinskiTriangle.getPath(), paint);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }


    private void update() {
        playerBall.update();
        if (!scorePoint.isActive()) {
            createNewScorePoint();
        } else {
            if (checkCollisionWithScorePoint()) {
                scorePoint.setActive(false);
                score+=2;
            }
        }
        while (triangleList.size() < NUMBER_OF_OBSTACLES) {
            createNewObstacle(150);
        }

        Iterator<SierpinskiTriangle> trianglesIterator = triangleList.iterator();

        while (trianglesIterator.hasNext()) {
            SierpinskiTriangle triangle = trianglesIterator.next();
            if(checkCollisionBallAndTriangle(playerBall, triangle)) {
                score--;
                timeLeft--;
                trianglesIterator.remove();
            }
        }

        if (timeLeft <= 0) {
            isPlaying = false;
            timeLeft = 0;
            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    showEndGameDialog("Game Over", "Your score: " + score);
                }
            });

            saveScore(score);
        }

    }

    private void saveScore(int score) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(c.getTime());
        SharedPreferences.Editor preferencesEditor = prefs.edit();
        Set<String> scores = new HashSet<>();
        Set<String> scoresFromPrefs = prefs.getStringSet(PREFERENCES_BEST_SCORES, scores);
        scoresFromPrefs.add(String.valueOf(score) + "," + date);
        preferencesEditor.putStringSet(PREFERENCES_BEST_SCORES, scoresFromPrefs);
        preferencesEditor.commit();
    }


    public void showEndGameDialog(String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity) context).finish();
            }
        });
        builder.show();
    }

    private void createNewObstacle(int size) {
        boolean created = false;
        while (!created) {
            boolean foundCollision = false;
            int x = rand(100, maxX - size);
            int y = rand(100, maxY - size);
            final SierpinskiTriangle sierpinskiTriangle = new SierpinskiTriangle(rand(1,4), x, y, size);

            for (int i = 0; i < triangleList.size() && !foundCollision; i++) {
                SierpinskiTriangle triangle = triangleList.get(i);

                Region clip = new Region(0, 0, maxX, maxY);

                Region region1 = new Region();
                region1.setPath(triangle.getPath(), clip);
                Region region2 = new Region();
                region2.setPath(sierpinskiTriangle.getPath(), clip);

                if (!region1.quickReject(region2) && region1.op(region2, Region.Op.INTERSECT)) {
                    foundCollision = true;
                }

            }

            if (checkCollisionBallAndTriangle(playerBall, sierpinskiTriangle)) {
                foundCollision = true;
            }

            if (checkCollisionBallAndTriangle(scorePoint, sierpinskiTriangle)) {
                foundCollision = true;
            }

            if (!foundCollision) {
                triangleList.add(sierpinskiTriangle);
                created = true;
            }
        }
    }

    public void pause() {
        gameTimer.cancel();
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        gameTimer = new Timer();
        registerGameTimer();
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void registerGameTimer() {
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLeft--;
            }
        }, 0, 1000);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerValues, 0, event.values.length);
            isAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerValues, 0, event.values.length);
            isMagnetometerSet = true;
        }

        if (isAccelerometerSet && isMagnetometerSet) {
            SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magnetometerValues);
            SensorManager.getOrientation(rotationMatrix, orientationMatrix);
            playerBall.setXa(orientationMatrix[2]);
            playerBall.setYa(orientationMatrix[1]);
        }
    }

    public void createNewScorePoint() {
        boolean created = false;
        boolean foundCollision = false;
        while (!created) {
            int x = rand((int) scorePoint.getRadius(), ((int) (maxX - scorePoint.getRadius())));
            int y = rand(100, (int) (maxY - scorePoint.getRadius()));
            scorePoint.setX(x);
            scorePoint.setY(y);

            for (SierpinskiTriangle triangle : triangleList) {
                if (checkCollisionBallAndTriangle(scorePoint, triangle)) {
                    foundCollision = true;
                    break;
                }
            }

            if (!foundCollision) {
                scorePoint.setActive(true);
                created = true;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private boolean checkCollisionBallAndTriangle(Ball ball, SierpinskiTriangle triangle) {
            Region clip = new Region(0, 0, maxX, maxY);

            Region region1 = new Region();
            region1.setPath(triangle.getPath(), clip);
            Region region2 = new Region();
            Path path = new Path();
            path.addCircle(ball.getX(), ball.getY(), ball.getRadius(), Path.Direction.CW);
            region2.setPath(path, clip);

        return !region1.quickReject(region2) && region1.op(region2, Region.Op.INTERSECT);
    }

    private boolean checkCollisionWithScorePoint() {
        float ballX = playerBall.getX();
        float ballY = playerBall.getY();
        float scorePointX = scorePoint.getX();
        float scorePointY = scorePoint.getY();

        float ballRadius = playerBall.getRadius();
        float scorePointRadius = scorePoint.getRadius();

        return distanceBetweenPoints(ballX, ballY, scorePointX, scorePointY) < ballRadius + scorePointRadius;
    }

    private double distanceBetweenPoints(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
