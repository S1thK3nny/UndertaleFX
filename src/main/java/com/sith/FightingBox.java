package com.sith;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.sith.Globals.DEVELOPER_MODE;

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

    public void updatePosition(double sceneWidth, double sceneHeight, double playerHeight, double playerWidth) {
        if(DEVELOPER_MODE) {
            handleDEVMove(sceneWidth, sceneHeight);
            handleDEVResize(playerHeight, playerWidth);
        }
    }

    private void handleDEVMove(double sceneWidth, double sceneHeight) {
        if(!devInputFBMoveUp() && !devInputFBMoveDown() && !devInputFBMoveLeft() && !devInputFBMoveRight()) {
            isMoving = false;
            return;
        }

        if(devInputFBMoveUp() && getY()>0) {
            setY(getY() - MOVE_AMOUNT);
        }

        if(devInputFBMoveDown() && sceneHeight-getY()>getHeight()) {
            setY(getY() + MOVE_AMOUNT);
        }

        // horizontal velocity
        if(devInputFBMoveLeft() && getX()>0) {
            setX(getX() - MOVE_AMOUNT);
        }

        if(devInputFBMoveRight() && sceneWidth-getX()>getWidth()) {
            setX(getX() + MOVE_AMOUNT);
        }

        isMoving = true;
    }

    private void handleDEVResize(double playerHeight, double playerWidth) {
        if(!devInputFBScaleHeightUp() && !devInputFBScaleHeightDown() && !devInputFBScaleWidthUp() && !devInputFBScaleWidthDown()) {
            isResizing = false;
            return;
        }

        if(devInputFBScaleHeightUp()) {
            setHeight(getHeight()+2);
            setY(getY()-1);
        }

        //Do not scale smaller than the player on height
        if(devInputFBScaleHeightDown() && getHeight()>(playerHeight + playerHeight/5)) {
            setHeight(getHeight()-2);
            setY(getY()+1);
        }

        if(devInputFBScaleWidthUp()) {
            setWidth(getWidth()+2);
            setX(getX()-1);
        }

        //Do not scale smaller than the player on width
        if(devInputFBScaleWidthDown() && getWidth()>(playerWidth + playerWidth/5)) {
            setWidth(getWidth()-2);
            setX(getX()+1);
        }
        isResizing = true;
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



    //      --  Dev input start     -- //

        //      --  Move input start     -- //
        private boolean devInputFBMoveUp() {return Main.keysPressed.contains("U");}

        private boolean devInputFBMoveLeft() {return Main.keysPressed.contains("H");}

        private boolean devInputFBMoveDown() {return Main.keysPressed.contains("J");}

        private boolean devInputFBMoveRight() {return Main.keysPressed.contains("K");}
        //      --  Move input end     -- //

        //      --  Scale input start     -- //
        private boolean devInputFBScaleHeightUp() {return Main.keysPressed.contains("NUMPAD8");}

        private boolean devInputFBScaleWidthUp() {return Main.keysPressed.contains("NUMPAD4");}

        private boolean devInputFBScaleHeightDown() {return Main.keysPressed.contains("NUMPAD5");}

        private boolean devInputFBScaleWidthDown() {return Main.keysPressed.contains("NUMPAD6");}
        //      --  Scale input end     -- //

    //      --  Dev input end     -- //
}