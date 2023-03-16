package com.sith.buttons;

import com.sith.globals;

public class attackButton extends interactiveButton {

    public attackButton() {
        super(globals.attackButton, globals.attackButtonSelected);
    }

    @Override
    public void openButton() {
        super.openButton();
        System.out.println("Attack button...");
        System.out.println("No functionality yet...");
    }
}