package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main extends Application {

    int width = 320;
    int height = 180;
    //int width = 640;
    //int height = 360;
    //{width=120;
    //height=120;}
    //120x120 is a factor of 1080,1440,1920 - 120x120 grids on their own thread might work

    final float diffusionSpread = 0.0005f;
    public static float fps = 30;
    public final float evaporateSpeed = 1f / (10f * (fps*1f));

    Group group = new Group();
    Scene scene = new Scene(group, width, height, Color.BLACK);

    Canvas canvas = new Canvas(width, height);
    //
    WritableImage writableImage = new WritableImage(width, height);

    Random random = new Random();
    Timeline drawer;

    ArrayList<Agent> agents = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        //System.out.println(evaporateSpeed);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case A:
                    for (Agent agent : agents) {
                        agent.velocity.set(0, 0);
                    }
                    break;
                case B:
                    for (Agent agent : agents) {
                        agent.update(90);
                    }
                    break;
                case P:
                    canvas.snapshot(null,writableImage);
                    //
                    try {
                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                        ImageIO.write(renderedImage, "png", new File("src\\images\\"+random.nextInt(10000)));
                    } catch (Exception s) {
                       s.printStackTrace();
                    }
            }
        });
        //
        group.getChildren().add(canvas);
        canvas.relocate(0, 0);


        for (int i = 0; i < 1000; i++) {
            agents.add(new Agent(width / 2, height / 2, writableImage));
            agents.get(agents.size() - 1).start();
        }

        this.drawer = new Timeline(new KeyFrame(Duration.seconds((float) 1 / fps), event -> {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {

                    float originalValue = (float) writableImage.getPixelReader().getColor(i,j).getOpacity();

                    float blurResult = getAVGOpacity(i,j);

                    float diffuseValue = Lerp(originalValue,blurResult,0.1f);

                    double diffuseAndEvaporateValue = Math.max(diffuseValue-evaporateSpeed,0);

                    writableImage.getPixelWriter().setColor(i, j, Color.rgb(255, 255, 255, diffuseAndEvaporateValue));
                }
            }
            //
            draw();
        }));
        this.drawer.setCycleCount(Timeline.INDEFINITE);
        this.drawer.play();


        stage.setScene(scene);
        stage.show();
    }

    public static float Clamp01(float value) {
        if (value < 0F)
            return 0F;
        else return Math.min(value, 1F);
    }

    // Interpolates between /a/ and /b/ by /t/. /t/ is clamped between 0 and 1.
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

    public static void main(String[] args) {
        launch(args);
    }
}