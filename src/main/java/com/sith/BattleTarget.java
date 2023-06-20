package com.sith;

import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import static com.sith.Main.fb;

public class BattleTarget extends Rectangle {
    public BattleTarget() {
        super(fb.getWidth() - fb.getWidth()/20, fb.getHeight()  - fb.getHeight()/10);
        setFill(new ImagePattern(Globals.battleTarget));

        //Add half of the width and height we just subtracted to the X and Y
        setX(fb.getX() + (fb.getWidth()/20)/2);
        setY(fb.getY() + (fb.getHeight()/10)/2);

        setVisible(false);
    }


}
