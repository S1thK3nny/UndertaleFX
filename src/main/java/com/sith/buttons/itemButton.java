package com.sith.buttons;

import com.sith.globals;

public class itemButton extends interactiveButton {

    public itemButton() {
        super(globals.itemButton, globals.itemButtonSelected);
    }

    @Override
    public void openButton() {
        super.openButton();
        System.out.println("Item button...");
        System.out.println("No functionality yet...");
    }

    public String interact(int option) {
        super.interact(option);
        if(option>=texts.size() || option<0) return super.interact(option);
        //Need the substring to ignore the "* "
        return "* You tried to " + texts.get(option).getText().substring(2) + "\n* ...we will see how that turns out!";
    }
}