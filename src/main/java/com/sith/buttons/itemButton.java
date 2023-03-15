package com.sith.buttons;

import com.sith.globals;

public class itemButton extends interactiveButton {

    public itemButton() {
        super(globals.itemButton, globals.itemButtonSelected);
    }

    @Override
    public void interact() {
        super.interact();
        System.out.println("Item button...");
    }
}