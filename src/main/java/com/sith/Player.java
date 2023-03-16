package com.sith;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.HashSet;

public class Player extends Rectangle {
    Rectangle collisionBox;

    private static final double MOVE_AMOUNT = 3; // how many pixels to move each time
    private static final double GRAVITY_AMOUNT = 3.5; // how many pixels to fall each time
    boolean showingCollision = false;
    boolean isMoving = false;
    boolean isJumping = false;

    public boolean isInvincible() {
        return invincible;
    }

    boolean invincible = false;

    private final String name = "CHARA";
    private final int LV = 19;
    private final int maxHealth;
    private int curHealth;

    String state = "normal";

    public Player(double x, double y, double width, double height) {
        super(x, y, width, height);
        addCollisionBox();
        setFill(new ImagePattern(globals.redHeart));

        int baseHealth = 20;
        if(LV==20) {
            maxHealth = 99;
        }
        else {
            maxHealth = baseHealth + ((LV-1)*4);
        }
        curHealth = maxHealth;
    }

    public void updatePosition(double sceneWidth, double sceneHeight, HashSet<String> keysPressed, Boolean[] borders, boolean wTimer) {
        boolean isShiftPressed = keysPressed.contains("SHIFT");
        double vy;
        double vx;

        // update velocity based on which keys are pressed

        if (keysPressed.contains("W") && !borders[2] && state.equals("normal")) {
            vy = -MOVE_AMOUNT;
        } else if (keysPressed.contains("S") && !borders[3]) {
            vy = MOVE_AMOUNT;
        } else {
            vy = 0;
        }

        // horizontal velocity
        if (keysPressed.contains("A") && !borders[0]) {
            vx = -MOVE_AMOUNT;
        } else if (keysPressed.contains("D") && !borders[1]) {
            vx = MOVE_AMOUNT;
        } else {
            vx = 0;
        }

        if(isShiftPressed) {
            vy /= 2;
            vx /= 2;
        }

        if(state.equals("gravity")) {
            if(borders[2]) wTimer = false;
            if(keysPressed.contains("W") && !borders[2] && !isJumping && wTimer) {
                vy = -GRAVITY_AMOUNT;
            }

            if(!borders[3] && !keysPressed.contains("W") || (!borders[3] && !wTimer)) {
                vy = GRAVITY_AMOUNT;
                isJumping = true;
            }
        }

        // update player position based on velocity
        setX(Math.min(Math.max(getX() + vx, 0), sceneWidth - getWidth()));
        setY(Math.min(Math.max(getY() + vy, 0), sceneHeight - getHeight()));
        collisionBox.setX(getX());
        collisionBox.setY(getY());

        if(!keysPressed.contains("W") && !keysPressed.contains("A") && !keysPressed.contains("S") && !keysPressed.contains("D")) {
            isMoving = false;
        }
        else if(keysPressed.contains("W") || keysPressed.contains("A") || keysPressed.contains("S") || keysPressed.contains("D")) {
            isMoving = true;
        }

        if(borders[3]) {
            // player has landed on the ground
            isJumping = false;
        }
    }

    public void drawCollision() {
        if(showingCollision) {
            collisionBox.setStroke(Color.TRANSPARENT);
            showingCollision = false;
        }
        else {
            collisionBox.setStroke(Color.WHITE);
            showingCollision = true;
        }
    }

    public void addCollisionBox() {
        collisionBox = new Rectangle(getX(), getY(), getBoundsInLocal().getWidth(), getBoundsInLocal().getHeight());
        collisionBox.setFill(Color.TRANSPARENT);
        collisionBox.setStroke(Color.TRANSPARENT);
        collisionBox.setStrokeWidth(3);
    }

    public void checkBounds(FightingBox fb, Boolean[] borders) {
        if(fb.getIsMoving() && fb.getIsResizing()) {
            if(borders[0]) setX(getX() + fb.getMoveAmount()*2);
            if(borders[1]) setX(getX() - fb.getMoveAmount()*2);
            if(borders[2]) setY(getY() + fb.getMoveAmount()*2);
            if(borders[3]) setY(getY() - fb.getMoveAmount()*2);
        }
        else if(fb.getIsMoving() || fb.getIsResizing()) {
            if(borders[0]) setX(getX() + fb.getMoveAmount());
            if(borders[1]) setX(getX() - fb.getMoveAmount());
            if(borders[2]) setY(getY() + fb.getMoveAmount());
            if(borders[3]) setY(getY() - fb.getMoveAmount());
        }
    }

    public void setState(String state) {
        switch (state) {
            case "normal", default -> setFill(new ImagePattern(globals.redHeart));
            case "gravity" -> setFill(new ImagePattern(globals.blueHeart));
        }
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getLV() {
        return LV;
    }

    public String getName() {
        return name;
    }

    public int getCurHealth() {
        return curHealth;
    }

    public void setCurHealth(int damage, boolean gotHit) {
        this.curHealth -= damage;
        Main.setHealthText(curHealth, maxHealth, damage);
        if(gotHit) {
            globals.hurtSound.play();
            hitEffect();
        }
    }

    public void hitEffect() {
        invincible = true;
        //Don't ever use timers for graphics changing stuff, use timelines!
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(0), e -> this.setVisible(false)),
                new KeyFrame(Duration.millis(110), e -> this.setVisible(true)),
                new KeyFrame(Duration.millis(220), e -> this.setVisible(false)),
                new KeyFrame(Duration.millis(330), e -> this.setVisible(true)),
                new KeyFrame(Duration.millis(330), e -> invincible = false)
        );
        timeline.play();
    }

    public void movePlayer(double x, double y) {
        setX(x);
        setY(y);
        collisionBox.setX(x);
        collisionBox.setY(y);
    }
}