package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

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


    WritableImage writableImage;

    public Agent(int startX, int startY, WritableImage writableImage) {
        this.threadName = size;
        size++;
        this.position = new Vecc2f(startX, startY);
        //
        float mult = 1.5f;
        this.velocity.random2D(mult);
        this.velocity.setMag(mult);
        //
        this.width = startX * 2;
        this.height = startY * 2;
        //
        this.writableImage = writableImage;
        //
        this.update = new Timeline(new KeyFrame(Duration.seconds((float) 1 / 30), event -> {

            update(width, height);
            try {
                this.writableImage.getPixelWriter().setColor((int) Math.min(position.x,width), (int) Math.min(position.y,height), Color.WHITE);
            } catch (IndexOutOfBoundsException e) {
                System.out.println(threadName + " " + this.position);
            }
            //draw();

        }));
        this.update.setCycleCount(Timeline.INDEFINITE);
        this.update.play();


    }

    public void update(int width, int height) {
        this.position.add(this.velocity);

        if ((int) this.position.x < 0 || (int) this.position.x >= width) {
            this.position.sub(this.velocity);
            this.velocity.x = (this.velocity.x * -1);
        }
        if ((int) this.position.y < 0 || (int) this.position.y >= height) {
            this.position.sub(this.velocity);
            this.velocity.y = (this.velocity.y * -1);

        }

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
