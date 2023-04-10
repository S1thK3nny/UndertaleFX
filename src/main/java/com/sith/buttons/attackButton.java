package com.sith.buttons;

import com.sith.globals;

public class attackButton extends interactiveButton {

    public attackButton() {
        super(globals.attackButton, globals.attackButtonSelected);
        needsSelectedEnemy = true;

    }

    @Override
    public void openButton() {
        super.openButton();
        updateEnemies();
        System.out.println("No functionality yet...");
    }
}