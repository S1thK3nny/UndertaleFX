package com.sith.buttons;

import com.sith.globals;

public class mercyButton extends interactiveButton {

    public mercyButton() {
        super(globals.mercyButton, globals.mercyButtonSelected);
    }

    @Override
    public void interact() {
        super.interact();
        System.out.println("Mercy button...");
    }
}