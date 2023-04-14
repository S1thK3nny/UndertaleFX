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
    private boolean typing = false;
    private int index = 0;

    private final Text currentText = new Text("* What do we have here? Let's have some fun! :)");
    String currentStringText = "* Let's get this over with...\n* Wait what?";
    Timeline timeline = new Timeline();

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

        if(keysPressed.contains("NUMPAD8")) {
            setHeight(getHeight()+2);
            setY(getY()-1);
        }

        if(keysPressed.contains("NUMPAD5") && getHeight()>(playerHeight + playerHeight/5)) {
            setHeight(getHeight()-2);
            setY(getY()+1);
        }

        if(keysPressed.contains("NUMPAD4")) {
            setWidth(getWidth()+2);
            setX(getX()-1);
        }

        if(keysPressed.contains("NUMPAD6") && getWidth()>(playerWidth + playerWidth/5)) {
            setWidth(getWidth()-2);
            setX(getX()+1);
        }

        if(!keysPressed.contains("NUMPAD8") && !keysPressed.contains("NUMPAD5") && !keysPressed.contains("NUMPAD4") && !keysPressed.contains("NUMPAD6")) {
            isResizing = false;
        }
        else if(keysPressed.contains("NUMPAD8") || keysPressed.contains("NUMPAD5") || keysPressed.contains("NUMPAD4") || keysPressed.contains("NUMPAD6")) {
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

    public void setCurrentTextVisible(boolean b) {
        currentText.setVisible(b);
        if(b) {
            displayText(currentStringText);
        }
        else {
            //Set Text for next round
            currentStringText = "* Let's get this over with...\n* Wait what?";
            stopTextTimeline();
        }
    }

    public void setCurrentTextVisible(boolean b, String s) {
        currentText.setVisible(b);
        currentStringText = s;
        if(b) {
            displayText(s);
        }
        else {
            stopTextTimeline();
        }
    }

    public void displayText(String s) {
        index = 0;
        currentText.setText("");
        timeline.getKeyFrames().clear();

        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(35), event -> {
                    typing = true;
                    currentText.setText(currentText.getText() + s.charAt(index));
                    ++index;
                    Globals.charAppearSound.play();
                }));

        timeline.setCycleCount(s.length());
        timeline.setOnFinished(e -> typing = false);
        timeline.play();
    }


    private void stopTextTimeline() {
        timeline.stop();
    }

    public void skipDialog() {
        if(!currentText.getText().equals(currentStringText)) {
            stopTextTimeline();
            // Print out the remaining letters
            currentText.setText(currentText.getText() + currentStringText.substring(index));
            typing = false;
        }
    }

    public boolean hasFinishedDialog() {
        return !typing;
    }
}