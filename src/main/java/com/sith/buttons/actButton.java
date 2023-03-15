package com.sith.buttons;

import com.sith.globals;

public class actButton extends interactiveButton {

    public actButton() {
        super(globals.actButton, globals.actButtonSelected);
    }

    @Override
    public void interact() {
        super.interact();
        System.out.println("Act button...");
    }
}