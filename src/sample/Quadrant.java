package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Quadrant implements Runnable {
    //
    String threadName;
    public Thread t;
    //
    int startX, startY, width, height;
    Timeline drawer;
    WritableImage writableImage;
    Canvas canvas;
    public static int size = 0;
    public static int complete = 0;

    public Quadrant(int startX, int startY, int width, int height, WritableImage writableImage, Canvas canvas) {
        this.threadName = "Quadrant " + size;
        size++;
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.writableImage = writableImage;
        this.canvas = canvas;
        System.out.println("StartX: " + startX + " StartY: " + startY + " W " + width + " H " + height);
        //

        //System.out.println("1");
    }

    @Override
    public void run() {
        try {
            this.drawer = new Timeline(new KeyFrame(Duration.seconds((float) 1 / Main.fps), event -> {
                updateQuad();

            }));
            this.drawer.setCycleCount(Timeline.INDEFINITE);
            complete++;
            //System.out.println("2");
        } catch (Exception e) {
        }
    }

    public void updateQuad() {
        for (int i = this.startX; i < this.startX + Main.quadWidth; i++) {
            for (int j = this.startY; j < this.startY + Main.quadHeight; j++) {

                float originalValue = (float) writableImage.getPixelReader().getColor(i, j).getOpacity();

                float blurResult = getAVGOpacity(i, j);

                float diffuseValue = Lerp(originalValue, blurResult, 0.1f);

                double diffuseAndEvaporateValue = Math.max(diffuseValue - Main.evaporateSpeed, 0);

                writableImage.getPixelWriter().setColor(i, j, Color.rgb(255, 255, 255, diffuseAndEvaporateValue));
            }
        }
        //
        draw(startX, startY);
    }

    public static void draw(int startX, int startY) {
        for (int i = startX; i < startX + Main.quadWidth; i++) {
            for (int j = startY; j < startY + Main.quadHeight; j++) {
                Main.canvas.getGraphicsContext2D().getPixelWriter().setColor(i, j, Main.writableImage.getPixelReader().getColor(i, j));
            }
        }
    }

    public void start() {

        System.out.println("Starting Quadrant " + threadName);
        if (t == null) {
            t = new Thread(this, String.valueOf(threadName));
            t.start();
        }

    }

    public void play() {

        this.drawer.play();
    }


    public static float Clamp01(float value) {
        if (value < 0F)
            return 0F;
        else return Math.min(value, 1F);
    }

    public static float Lerp(float a, float b, float t) {
        return a + ((b - a) * Clamp01(t));
    }

    private float getAVGOpacity(int i, int j) {
        float a = 0;
        //
        for (int k = -1; k <= 1; k++) {
            for (int l = -1; l <= 1; l++) {
                if (i + k > 0 && i + k < width && j + l > 0 && j + l < height) {
                    a += writableImage.getPixelReader().getColor(i + k, j + l).getOpacity();
                }
            }
        }
        //
        a /= 9f;

        return a;
    }

    private void draw() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                canvas.getGraphicsContext2D().getPixelWriter().setColor(i, j, writableImage.getPixelReader().getColor(i, j));
            }
        }
    }


}
