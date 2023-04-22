package com.sith.enemies;

import com.sith.Globals;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Enemy extends Rectangle {
    protected String name;
    protected final ArrayList<String> actOptions = new ArrayList<>();
    protected Image[] sprites;
    protected Image currentSprite;
    protected Image currentHurtSprite;
    protected int atk;
    protected int def;
    protected int hp;
    protected int goldOnWin;
    protected String checkDescription;
    protected boolean canBeSpared = false;

    private final ArrayList<Enemy> enemies;

    public Enemy(ArrayList<Enemy> enemies, HBox enemiesBox, Image[] sprites, String name, int atk, int def) {
        this.name = name;
        this.sprites = sprites;
        this.atk = atk;
        this.def = def;

        currentSprite = sprites[0];
        currentHurtSprite = sprites[1];

        actOptions.add("Check");
        checkDescription = "\n* A cotton heart and a button eye\n* You are the apple of my eye";

        setFill(new ImagePattern(currentSprite));
        setWidth(currentSprite.getWidth() *1.5);
        setHeight(currentSprite.getHeight() *1.5);

        this.enemies = enemies;

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
                ", currentSprite=" + currentSprite +
                ", currentHurtSprite=" + currentHurtSprite +
                ", atk=" + atk +
                ", def=" + def +
                ", hp=" + hp +
                ", goldOnWin=" + goldOnWin +
                ", canBeSpared=" + canBeSpared +
                '}';
    }

    public void setCurrentSprite(Image currentSprite, Image currentHurtSprite) {
        setFill(new ImagePattern(currentSprite));
        this.currentHurtSprite = currentHurtSprite;
        setWidth(currentSprite.getWidth() *1.5);
        setHeight(currentSprite.getHeight() *1.5);
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

    public boolean canBeSpared() {
        return canBeSpared;
    }

    public void spareEnemy() {
        enemies.remove(this);
        Globals.vaporizedSound.play();
        setOpacity(0.5);
        //Still missing the spare visual effect
    }
}
