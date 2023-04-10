package com.sith.enemies;

import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;

public class Enemy extends Rectangle {
    protected String name;
    protected final ArrayList<String> actOptions = new ArrayList<>();
    protected Image[] sprites;
    protected Image hurtVersion;
    protected int atk;
    protected int def;
    protected String checkDescription;

    public Enemy(Pane root, ArrayList<Enemy> enemies, HBox enemiesBox, Image[] sprites, String name, int atk, int def) {
        this.name = name;
        this.sprites = sprites;
        this.atk = atk;
        this.def = def;

        hurtVersion = sprites[1];

        actOptions.add("Check");
        checkDescription = "\n* A cotton heart and a button eye\n* You are the apple of my eye";

        setFill(new ImagePattern(sprites[0]));
        setWidth(sprites[0].getWidth() *1.5);
        setHeight(sprites[0].getHeight() *1.5);

        root.getChildren().add(this);
        enemies.add(this);
        enemiesBox.getChildren().add(this);
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getActOptions() {
        return actOptions;
    }

    @Override
    public String toString() {
        return "Enemy{" +
                "name='" + name + '\'' +
                '}';
    }

    protected String actOption1() {
        return name + " - ATK " + atk + " DEF " + def + checkDescription;
    }

    protected String actOption2() {
        return "actOption2";
    }

    protected String actOption3() {
        return "actOption3";
    }

    protected String actOption4() {
        return "actOption4";
    }

    public String playerActed(int actOption) {
        switch (actOption) {
            case 0 -> {
                return actOption1();
            }
            case 1 -> {
                return actOption2();
            }
            case 2 -> {
                return actOption3();
            }
            case 3 -> {
                return actOption4();
            }
            default -> {
                return "Hey! Something went wrong when acting...";
            }
        }
    }

    public void setCheckDescription(String checkDescription) {
        this.checkDescription = checkDescription;
    }
}
