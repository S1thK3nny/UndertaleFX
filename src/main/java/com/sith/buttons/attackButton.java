package com.sith.buttons;

import com.sith.globals;

public class attackButton extends interactiveButton {

    public attackButton() {
        super(globals.attackButton, globals.attackButtonSelected);
    }

    @Override
    public void interact() {
        super.interact();
        System.out.println("Attack button...");
    }
}