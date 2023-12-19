package com.sith.buttons;

import com.sith.Main;
import com.sith.Globals;
import javafx.scene.text.Text;

public class actButton extends interactiveButton {
    double playerWidth;

    public actButton(double playerWidth) {
        super(Globals.actButton, Globals.actButtonSelected);
        needsSelectedEnemy = true;
        this.playerWidth = playerWidth;
        updateEnemies();
    }

    @Override
    public void openButton() {
        super.openButton();
        updateEnemies();
    }

    public String interact() {
        super.interact();
        if(option>=texts.size() || option<0) return super.interact();
        return "* " + Main.enemies.get(selectedEnemy).playerActed(option);
        //return "* You tried to " + texts.get(option).getText().substring(2) + "\n* ...we will see how that turns out!";
    }

    @Override
    public void actionAfterEnemySelected() {
        super.actionAfterEnemySelected();
        texts.clear();
        for(int i = 0; i< Main.enemies.get(selectedEnemy).getActOptionsDisplayName().size(); i++) {
            Text t = new Text("* " + Main.enemies.get(selectedEnemy).getActOptionsDisplayName().get(i));
            texts.add(t);
        }

        firstRow.getChildren().clear();
        secondRow.getChildren().clear();

        for (int i = 0; i < texts.size(); i++) {
            if (i % 2 == 0) {
                firstRow.getChildren().add(texts.get(i));
            } else {
                secondRow.getChildren().add((texts.get(i)));
            }
        }
        configureText(playerWidth);

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}