package com.sith.buttons;

import com.sith.Globals;

public class attackButton extends interactiveButton {

    public attackButton() {
        super(Globals.attackButton, Globals.attackButtonSelected);
        needsSelectedEnemy = true;
        wantsToReturnTextAfterUsage = false;
    }

    @Override
    public void openButton() {
        super.openButton();
        updateEnemies();
        System.out.println("No functionality yet...");
    }
}