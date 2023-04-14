package com.sith;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.HashSet;

public class test extends Application {

    final HashSet<String> keysPressed = new HashSet<>();
    int fill = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Pane root = new Pane();

        Rectangle redHeart = new Rectangle(100, 100, 200, 200);
        Rectangle blueHeart = new Rectangle(redHeart.getX(), redHeart.getY(), redHeart.getWidth(), redHeart.getHeight());
        Rectangle clip = new Rectangle(blueHeart.getX(), blueHeart.getY(), blueHeart.getWidth(), fill);

        redHeart.setFill(new ImagePattern(Globals.redHeart));
        blueHeart.setFill(new ImagePattern(Globals.blueHeart));
        clip.setFill(Color.WHITE);

        blueHeart.setClip(clip);

        root.getChildren().addAll(redHeart, blueHeart);
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnKeyPressed(event -> {
            keysPressed.add(event.getCode().toString());
        });

        scene.setOnKeyReleased(event -> keysPressed.remove(event.getCode().toString()));

        new AnimationTimer() {
            public void handle(long nano) {
                if(keysPressed.contains("W") && fill>=0) fill--;
                if(keysPressed.contains("S") && fill<redHeart.getHeight()) fill++;
                System.out.println(fill);
                clip.setHeight(fill);
            }
        }.start();

    }
}
