package com.sith.buttons;

import com.sith.globals;
import javafx.scene.text.Text;

public class mercyButton extends interactiveButton {
    Text t;
    Text t2;

    //I HATE THIS BUTTON I HATE THIS BUTTON I HATE THIS BUTTON
    //THE DAMN PAIN IT HAS CAUSED ME TO TRACK ALL THIS IS INTENSE
    //INVISIBLE OPTIONS ARE A THING NOW
    //HOW WILL I DO IT FOR THE ITEMS BUTTON? NO IDEA
    public mercyButton(double playerWidth) {
        super(globals.mercyButton, globals.mercyButtonSelected);

        t = new Text("* Spare");
        t2 = new Text("* Flee");

        texts.add(t);
        texts.add(t2);

        configureText(playerWidth);

        firstRow.getChildren().addAll(t, t2);
    }

    @Override
    public void openButton() {
        super.openButton();
        System.out.println("Mercy button...");
        System.out.println("No functionality yet...");
    }

    public String interact(int option) {
        super.interact(option);
        if(option>=texts.size() || option<0) return super.interact(option);
        return "* You tried to " + texts.get(option).getText().substring(2) + "\n* ...we will see how that turns out!";
    }
}