package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {

   int width = 320;
   int height = 180;
    //int width = 640;
    //int height = 360;

    Group group = new Group();
    Scene scene = new Scene(group, width, height);

    Canvas canvas = new Canvas(width, height);
    //
    WritableImage writableImage = new WritableImage(width, height);

    Random random = new Random();
    Timeline drawer;

    ArrayList<Agent> agents = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {


        group.getChildren().add(canvas);
        canvas.getGraphicsContext2D().getPixelWriter().setColor(100, 100, writableImage.getPixelReader().getColor(100, 100));
        canvas.relocate(0, 0);
        canvas.getGraphicsContext2D().fillRect(0, 0, width, height);
        draw(Color.BLACK);

        //for (int i = 0; i <10 ; i++) {
        //    writableImage.getPixelWriter().setColor(random.nextInt(width), random.nextInt(height),Color.BLACK );
        //}

        for (int i = 0; i < 255; i++) {
            agents.add(new Agent(width / 2, height / 2,writableImage));
            agents.get(agents.size() - 1).start();
        }


        this.drawer = new Timeline(new KeyFrame(Duration.seconds((float) 1 / 30), event -> {
            diffuse();
            draw();
        }));
        this.drawer.setCycleCount(Timeline.INDEFINITE);
        this.drawer.play();


        //draw();

        stage.setScene(scene);
        stage.show();
    }

    private void diffuse() {


    }

    private void draw() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                canvas.getGraphicsContext2D().getPixelWriter().setColor(i, j, writableImage.getPixelReader().getColor(i, j));
            }
        }
    }

    private void draw(Color a) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                writableImage.getPixelWriter().setColor(i, j, Color.rgb(0,0,0,1));

                canvas.getGraphicsContext2D().getPixelWriter().setColor(i, j, a);
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
