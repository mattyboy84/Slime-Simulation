package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.util.Duration;

import java.util.Random;

public class Agent implements Runnable {

    Vecc2f position;
    Vecc2f velocity = new Vecc2f();
    Vecc2f steering = new Vecc2f();
    //
    String threadName;
    public Thread t;
    Timeline update;
    //
    int width, height;
    int startX, startY;

    public static int size = 0;
    float mult = 1.5f * (30 / Main.fps);
    float angleDelta = 20;
    float angle;
    float weightForward = 0, weightLeft = 0, weightRight = 0;
    public int viewDis = 15;
    int sensorSize = 1;
    float turnSpeed = 8f * (30 / Main.fps);
    Random random = new Random();
    public static int complete = 0;
    WritableImage writableImage;

    public Agent(int startX, int startY, int width, int height, WritableImage writableImage) {
        this.threadName = "Agent " + size;
        size++;
        //
        this.startX = startX;
        this.startY = startY;
        this.position = new Vecc2f(this.startX, this.startY);
        //

        this.velocity.random2D(mult);
        this.velocity.setMag(mult);
        //

        this.width = width;
        this.height = height;
        //
        this.writableImage = writableImage;
        //
    }

    private void steer() {
        weightForward = sense(0);
        weightLeft = sense(angleDelta);
        weightRight = sense(-angleDelta);
        //System.out.println("Left: " + weightLeft + " Middle: " + weightForward + " Right: " + weightRight);

        if (weightForward < weightLeft && weightForward < weightRight) {
            this.velocity.fromAngle((this.velocity.toAngle() + ((((random.nextFloat() - 0.5)) * 3.5f) * (6f * 1))));
        } else if (weightRight > weightLeft) {
            this.velocity.fromAngle((this.velocity.toAngle() - (random.nextFloat() * (turnSpeed * 1))));
        } else if (weightLeft > weightRight) {
            this.velocity.fromAngle((this.velocity.toAngle() + (random.nextFloat() * (turnSpeed * 1))));
        }
    }

    private float sense(float angleDelta) {
        float a = 0f;
        //
        float angle = this.velocity.toAngle() + angleDelta;
        Vecc2f sensorDir = new Vecc2f(Math.cos(Math.toRadians(angle)), Math.sin(Math.toRadians(angle)));
        sensorDir.setMag(viewDis);
        //System.out.println(sensorDir);
        for (int i = (int) (position.x + sensorDir.x - (sensorSize / 2)); i < (int) (position.x + sensorDir.x + sensorSize + (sensorSize / 2)); i++) {
            for (int j = (int) (position.y + sensorDir.y - (sensorSize / 2)); j < (int) (position.y + sensorDir.y + sensorSize + (sensorSize / 2)); j++) {
                if (i > 0 && i < width && j > 0 && j < height) {
                    a += writableImage.getPixelReader().getColor(i, j).getOpacity();
                }
            }
        }

        return a;
    }

    public void update() {
        this.position.add(this.velocity);

        if ((int) this.position.x < 0 || (int) this.position.x >= this.width || (int) this.position.y < 0 || (int) this.position.y >= this.height) {
            this.position.x = Math.min(width - 0.01f, Math.max(0, this.position.x));
            this.position.y = Math.min(height - 0.01f, Math.max(0, this.position.y));
            this.velocity.random2D(mult);
            this.velocity.setMag(mult);
        }
    }

    public void update(double i) {
        this.velocity.fromAngle(i);

    }

    @Override
    public void run() {
        try{this.update = new Timeline(new KeyFrame(Duration.seconds((float) 1 / Main.fps), event -> {

            //
            steer();
            //
            update();
            this.writableImage.getPixelWriter().setColor((int) Math.min(position.x, width), (int) Math.min(position.y, height), Color.rgb(255, 255, 255, 1));

        }));
        this.update.setCycleCount(Timeline.INDEFINITE);
        complete++;
//play();
    }catch (Exception e){

    }}

    public void start() {

        System.out.println("Starting Agent " + threadName);
        if (t == null) {
            t = new Thread(this, String.valueOf(threadName));
            t.start();
        }
    }


    public void play() {
        this.update.play();
    }
}