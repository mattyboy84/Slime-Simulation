package sample;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {

    public static int quadWidth = 120;
    public static int quadHeight = 120;
    public static int quadX = 3, quadY = 3;
    public static int width = quadWidth * quadX;
    public static int height = quadHeight * quadY;

    //int width = 640;
    //int height = 360;

    //120x120 is a factor of 1080,1440,1920 - 120x120 grids on their own thread might work

    public static float fps = 30;
    public static float evaporateSpeed = 1f / (10f * (fps * 1f));

    Group group = new Group();
    Scene scene = new Scene(group, width, height, Color.BLACK);

    static Canvas canvas = new Canvas(width, height);
    //
    static WritableImage writableImage = new WritableImage(width, height);

    Random random = new Random();
    Timeline drawer;

    ArrayList<Agent> agents = new ArrayList<>();
    ArrayList<Quadrant> quadrants = new ArrayList<>();

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
                    canvas.snapshot(null, writableImage);
                    //
                    try {
                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                        ImageIO.write(renderedImage, "png", new File("src\\images\\" + random.nextInt(10000)));
                    } catch (Exception s) {
                        s.printStackTrace();
                    }
                case D:
                    break;
            }
        });
        //
        //Quadrant quadrant = new Quadrant(0, 0, width, height, writableImage, canvas);
        //quadrant.play();
        canvas.relocate(0, 0);
        group.getChildren().add(canvas);
        //
        for (int i = 0; i < quadX; i++) {
            for (int j = 0; j < quadY; j++) {
                quadrants.add(new Quadrant(i * quadWidth, j * quadHeight, width, height, writableImage, canvas));
                quadrants.get(quadrants.size() - 1).start();
                //quadrants.get(quadrants.size()-1).play();
            }
        }
        //for (Quadrant quadrant : quadrants) {
        //    quadrant.start();
        //    //quadrant.play();
        //}

        //
        for (int i = 0; i < 4000; i++) {
            agents.add(new Agent(width / 2, height / 2, width, height, writableImage));
            agents.get(agents.size() - 1).start();
            //agents.get(agents.size()-1).play();
        }

        while ((quadrants.size() != Quadrant.complete) && (agents.size() != Agent.complete)) {
            Thread.sleep(50);
        }

        System.out.println(Quadrant.complete);
        System.out.println(Agent.complete);
        for (int i = 0; i < quadrants.size(); i++) {
            quadrants.get(i).play();
        }
        for (int i = 0; i < agents.size(); i++) {
            agents.get(i).play();
        }
//
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}