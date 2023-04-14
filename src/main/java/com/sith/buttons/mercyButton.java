package com.sith.buttons;

import com.sith.Main;
import com.sith.enemies.Enemy;
import com.sith.Globals;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class mercyButton extends interactiveButton {
    Text t;
    Text t2;

    /*Alright, this button is a little different but that is fine.
    The Spare and Flee will have to use the enemy options, and the enemies will use the int option instead.
     */

    public mercyButton(double playerWidth) {
        super(Globals.mercyButton, Globals.mercyButtonSelected);
        needsSelectedEnemy = false;

        t = new Text("* Spare");
        t2 = new Text("* Flee");

        texts.add(t);
        texts.add(t2);

        configureText(playerWidth);

        firstRow.getChildren().addAll(t, t2);
        wantsToReturnTextAfterUsage = false;
    }

    @Override
    public void openButton() {
        super.openButton();
        spareOrFlee();
    }

    public void spareOrFlee() {
        firstRow.getChildren().clear();
        secondRow.getChildren().clear();
        texts.clear();

        texts.add(t);
        texts.add(t2);
        firstRow.getChildren().addAll(t, t2);

        t.setFill(Color.WHITE);
        for(Enemy enemy: Main.enemies) {
            if(enemy.canBeSpared()) {
                t.setFill(Color.YELLOW);
            }
        }
    }

    public String interact() {
        super.interact();
        if(option>=texts.size() || option<0) return super.interact();
        if (option == 0) {
            //if we do not do it like this, we will get a ConcurrentModificationException
            List<Enemy> enemiesCopy = new ArrayList<>(Main.enemies);
            for (Enemy enemy : enemiesCopy) {
                if (enemy.canBeSpared()) {
                    enemy.spareEnemy();
                    Main.enemies.remove(enemy);
                    return "* Congratulations on sparing someone!";
                }
            }
            return "* No one to spare...";
        }
        else {
            return "* You feel tired, maybe fleeing is not \n  the best option right now";
        }
    }
}