package com.sith.enemies;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;

public class Enemy extends Rectangle {
    public Enemy(Pane root, ArrayList<Enemy> enemies, Image[] sprites) {

        setFill(new ImagePattern(sprites[0]));
        setWidth(sprites[0].getWidth() *1.5);
        setHeight(sprites[0].getHeight() *1.5);

        root.getChildren().add(this);
        enemies.add(this);
    }
}
