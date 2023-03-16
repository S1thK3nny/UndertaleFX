package com.sith.buttons;

import com.sith.globals;
import javafx.scene.text.Text;

public class actButton extends interactiveButton {
    Text t;
    Text t1;
    Text t2;
    Text t3;

    public actButton(double playerWidth) {
        super(globals.actButton, globals.actButtonSelected);

        t = new Text("* Check");
        t1 = new Text("* Threaten");
        t2 = new Text("* Plan");
        t3 = new Text("* Prepare");
        texts.add(t);
        texts.add(t1);
        texts.add(t2);
        texts.add(t3);

        configureText(playerWidth);

        firstRow.getChildren().addAll(t,t1);
        secondRow.getChildren().addAll(t2, t3);
    }

    @Override
    public void interact() {
        super.interact();

    }
}