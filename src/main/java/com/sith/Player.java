package com.sith;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.sith.Main.userInputTop;
import static com.sith.Main.userInputLeft;
import static com.sith.Main.userInputDown;
import static com.sith.Main.userInputRight;
import static com.sith.Main.userInputBack;

public class Player extends Rectangle {

    public enum State {
        NORMAL,
        GRAVITY,
        GONE,
        MENU
    }

    private static final double MOVE_AMOUNT = 3; // how many pixels to move each time
    private static final double GRAVITY_AMOUNT = 3.5; // how many pixels to fall each time

    private final int MAX_LEVEL = 20;
    private final ImagePattern ICON_REDHEART = new ImagePattern(Globals.redHeart);
    private final ImagePattern ICON_BLUEHEART = new ImagePattern(Globals.blueHeart);

    boolean isMoving = false;
    boolean isJumping = false;

    private boolean wentIntoButton;

    public boolean isInvincible() {
        return invincible;
    }

    boolean invincible = false;

    private final String name = "CHARA";
    Enum<State> state = State.NORMAL;

    private int LV = 7;
    private int maxHealth;
    private int curHealth;



    public Player(double x, double y, double width, double height) {
        super(x, y, width, height);
        setFill(ICON_REDHEART);
        setupHealth();
    }



    // Deal with health setup start
    public void setupHealth() {
        int baseHealth = 20;
        maxHealth = calculateMaxHealth(baseHealth);
        curHealth = calculateInitialHealth();
    }

    private int calculateMaxHealth(int baseHealth) {
        return (LV == MAX_LEVEL) ? 99 : baseHealth + ((LV - 1) * 4);
    }

    private int calculateInitialHealth() {
        if (curHealth == 0 || curHealth >= maxHealth) {
            return maxHealth;
        } else {
            curHealth += 4;
            if (LV == MAX_LEVEL) {
                curHealth += 3;
            }
            return curHealth;
        }
    }
    // Deal with health setup end



    public void updatePosition(double sceneWidth, double sceneHeight, Boolean[] borders, boolean wTimer) {
        double vy;
        double vx;

        // update velocity based on which keys are pressed

        if (userInputTop() && !borders[2] && state == State.NORMAL) {
            vy = -MOVE_AMOUNT;
        } else if (userInputDown() && !borders[3]) {
            vy = MOVE_AMOUNT;
        } else {
            vy = 0;
        }

        // horizontal velocity
        if (userInputLeft() && !borders[0]) {
            vx = -MOVE_AMOUNT;
        } else if (userInputRight() && !borders[1]) {
            vx = MOVE_AMOUNT;
        } else {
            vx = 0;
        }

        if(userInputBack()) {
            vy /= 2;
            vx /= 2;
        }

        if(state == State.GRAVITY) {
            if(borders[2]) wTimer = false;
            if(userInputTop() && !borders[2] && !isJumping && wTimer) {
                vy = -GRAVITY_AMOUNT;
            }

            if(!borders[3] && !userInputTop() || (!borders[3] && !wTimer)) {
                vy = GRAVITY_AMOUNT;
                isJumping = true;
            }
        }

        // update player position based on velocity
        setX(Math.min(Math.max(getX() + vx, 0), sceneWidth - getWidth()));
        setY(Math.min(Math.max(getY() + vy, 0), sceneHeight - getHeight()));

        isMoving = userInputTop() || userInputLeft() || userInputDown() || userInputRight();

        if(borders[3]) {
            // player has landed on the ground
            isJumping = false;
        }
    }

    public void checkBounds(FightingBox fb, Boolean[] borders) {
        double moveAmount = fb.getMoveAmount();
        double adjustment = fb.getIsMoving() && fb.getIsResizing() ? moveAmount * 2 : moveAmount;

        if (borders[0]) setX(getX() + adjustment);
        if (borders[1]) setX(getX() - adjustment);
        if (borders[2]) setY(getY() + adjustment);
        if (borders[3]) setY(getY() - adjustment);
    }

    public void setState(State state) {
        setPlayerState(
                switch (state) {
                    case NORMAL, default -> ICON_REDHEART;
                    case GRAVITY -> ICON_BLUEHEART;
                    case GONE -> null;
                },
                state != State.GONE
        );
        this.state = state;
    }

    private void setPlayerState(ImagePattern fill, boolean isVisible) {
        setFill(fill);
        setVisible(isVisible);
    }

    public Enum<State> getState() {
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
        // Don't want to land below 0 health
        if(curHealth>0) {
            this.curHealth -= damage;
            Main.setHealthText(curHealth, maxHealth, damage, true);
        }
        if(gotHit) {
            Globals.hurtSound.play();
            hitEffect();
        }
    }

    public boolean getWentIntoButton() {
        return wentIntoButton;
    }

    public void setWentIntoButton(boolean wentIntoButton) {
        this.wentIntoButton = wentIntoButton;
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
    }

    public void healPlayer(int healpoints) {
        restoreHealth(healpoints);
        Main.setHealthText(curHealth, maxHealth, healpoints, false);
        Globals.healSound.play();
    }

    public void restoreHealth(int healpoints) {
        if(curHealth+healpoints<maxHealth) {
            curHealth+=healpoints;
        }
        else {
            curHealth = maxHealth;
        }
    }

    public void increaseLV() {
        if(!(LV>=MAX_LEVEL)) {
            LV++;
            Globals.levelUpSound.play();
            setupHealth();
        }
    }

    public void decreaseLV() {
        if(!(LV<=1)) {
            LV--;
            setupHealth();
        }
    }
}