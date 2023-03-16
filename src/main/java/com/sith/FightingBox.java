package com.sith;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.HashSet;

public class FightingBox extends Rectangle {

    private static final int MOVE_AMOUNT = 3; // how many pixels to move each time
    private boolean isMoving = false;
    private boolean isResizing = false;
    private int index = 0;

    private final Text currentText = new Text("* What do we have here? Let's have some fun! :)");
    String currentStringText = "* Let's get this over with...\n* Wait what?";
    Timeline timeline;

    public FightingBox(double x, double y, double width, double height) {
        super(x, y, width, height);
        setFill(Color.TRANSPARENT);
        setStroke(Color.WHITE);
        setStrokeWidth(10);

        Main.configureText(currentText);
        currentText.setTranslateX(getX() + getStrokeWidth() + currentText.getBoundsInLocal().getHeight()/2);
        currentText.setLayoutY(getY() + getStrokeWidth() + currentText.getBoundsInLocal().getHeight());
        currentText.setVisible(false);

    }

    public void updatePosition(double sceneWidth, double sceneHeight, HashSet<String> keysPressed, double playerHeight, double playerWidth) {
        // update velocity based on which keys are pressed
        // vertical velocity
        if (keysPressed.contains("U") && getY()>0) {
            setY(getY() - MOVE_AMOUNT);
        } else if (keysPressed.contains("J") && sceneHeight-getY()>getHeight()) {
            setY(getY() + MOVE_AMOUNT);
        }

        // horizontal velocity
        if (keysPressed.contains("H") && getX()>0) {
            setX(getX() - MOVE_AMOUNT);
        } else if (keysPressed.contains("K") && sceneWidth-getX()>getWidth()) {
            setX(getX() + MOVE_AMOUNT);
        }

        if(!keysPressed.contains("U") && !keysPressed.contains("J") && !keysPressed.contains("H") && !keysPressed.contains("K")) {
            isMoving = false;
        }
        else if(keysPressed.contains("U") || keysPressed.contains("J") || keysPressed.contains("H") || keysPressed.contains("K")) {
            isMoving = true;
        }

        if(keysPressed.contains("UP")) {
            setHeight(getHeight()+2);
            setY(getY()-1);
        }

        if(keysPressed.contains("DOWN") && getHeight()>(playerHeight + playerHeight/5)) {
            setHeight(getHeight()-2);
            setY(getY()+1);
        }

        if(keysPressed.contains("LEFT")) {
            setWidth(getWidth()+2);
            setX(getX()-1);
        }

        if(keysPressed.contains("RIGHT") && getWidth()>(playerWidth + playerWidth/5)) {
            setWidth(getWidth()-2);
            setX(getX()+1);
        }

        if(!keysPressed.contains("UP") && !keysPressed.contains("DOWN") && !keysPressed.contains("LEFT") && !keysPressed.contains("RIGHT")) {
            isResizing = false;
        }
        else if(keysPressed.contains("UP") || keysPressed.contains("DOWN") || keysPressed.contains("LEFT") || keysPressed.contains("RIGHT")) {
            isResizing = true;
        }
    }

    public boolean getIsMoving() {
        return isMoving;
    }

    public int getMoveAmount() {
        return MOVE_AMOUNT;
    }

    public boolean getIsResizing() {
        return isResizing;
    }

    public Text getCurrentText() {
        return currentText;
    }

    public void setIsResizing(boolean isResizing) {
        this.isResizing = isResizing;
    }

    public void setCurrentText(String text) {
        currentStringText = text;
    }

    public void setCurrentTextVisible(boolean b) {
        currentText.setVisible(b);
        if(b) displayText();
    }

    public void displayText() {
        index = 0;
        currentText.setText("");
        timeline = new Timeline(
                new KeyFrame(Duration.millis(30), event -> {
                    currentText.setText(currentText.getText() + currentStringText.charAt(index));
                    ++index;
                    globals.charAppearSound.play();
                })
        );

        timeline.setCycleCount(currentStringText.length());
        timeline.play();
    }

    public void stopTextTimeline() {
        timeline.stop();
    }
}