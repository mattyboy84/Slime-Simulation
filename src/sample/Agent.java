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
    int threadName;
    public Thread t;
    Timeline update;
    //
    int width, height;

    public static int size = 0;
    float mult = 1.5f * (30 / Main.fps);
    float angleDelta = 45;
    float angle;
    float weightForward = 0, weightLeft = 0, weightRight = 0;
    public int viewDis = 8;
    int sensorSize = 4;
    float turnSpeed = 2f * (30 / Main.fps);
    Random random = new Random();

    WritableImage writableImage;

    public Agent(int startX, int startY, WritableImage writableImage) {
        this.threadName = size;
        size++;
        this.position = new Vecc2f(startX, startY);
        //

        this.velocity.random2D(mult);
        this.velocity.setMag(mult);
        //
        this.width = startX * 2;
        this.height = startY * 2;
        //
        this.writableImage = writableImage;
        //
        this.update = new Timeline(new KeyFrame(Duration.seconds((float) 1 / Main.fps), event -> {
            //
            steer();
            //
            update(width, height);
            this.writableImage.getPixelWriter().setColor((int) Math.min(position.x, width), (int) Math.min(position.y, height), Color.rgb(255, 255, 255, 1));

        }));
        this.update.setCycleCount(Timeline.INDEFINITE);
        this.update.play();

    }

    private void steer() {
        //System.out.println("-------------------");
        weightForward = sense(0);
        weightLeft = sense(-angleDelta);
        weightLeft = sense(angleDelta);
        //System.out.println("end -------------------");
        //System.out.println("Left: " + weightLeft + " Middle: " + weightForward + " Right: " + weightRight);

        if (weightForward < weightLeft && weightForward > weightRight) {

        } else if (weightForward <= weightLeft && weightForward <= weightRight) {
            this.velocity.fromAngle((this.velocity.toAngle()+ ((((random.nextInt(1)-0.5)/4))*turnSpeed)));
        } else if (weightRight > weightLeft) {
            this.velocity.fromAngle((this.velocity.toAngle()-(random.nextFloat()*(turnSpeed*2))));
        } else if (weightLeft > weightRight) {
            this.velocity.fromAngle((this.velocity.toAngle()+(random.nextFloat()*(turnSpeed*2))));

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

    public void update(int width, int height) {
        this.position.add(this.velocity);

        if ((int) this.position.x < 0 || (int) this.position.x >= width || (int) this.position.y < 0 || (int) this.position.y >= height) {
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
    }

    public void start() {

        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, String.valueOf(threadName));
            t.start();
        }
    }


}