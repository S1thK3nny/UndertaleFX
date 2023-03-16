package com.sith.enemies;

import com.sith.FightingBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class Projectile extends Rectangle {

    List<Projectile> projectiles;
    protected final double MOVE_AMOUNT = 3;

    public Projectile(List<Projectile> projectiles, Pane root, FightingBox box, double y, double width, double height) {
        super((box.getX() + ((box.getWidth()-width) * Math.random())), y, width, height);
        /*
        I swear to god, do not, never EVER, touch the X again for the super. It has been tested, it will always spawn inside the fb.
        DO NOT MESS WITH IT AGAIN KENNY
         */
        setFill(Color.WHITE);
        root.getChildren().add(this);
        this.projectiles = projectiles;
        addToList();
    }

    public void move() {
        setY(getY() + randomMoveAmount());
    }

    public double randomMoveAmount() {
        return MOVE_AMOUNT + MOVE_AMOUNT*Math.random();
    }

    public void addToList() {
        projectiles.add(this);
    }

    public void removeFromList() {
        projectiles.remove(this);
    }
}
