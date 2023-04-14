package com.sith.buttons;

import com.sith.Globals;
import com.sith.Main;
import com.sith.Player;
import javafx.scene.text.Text;

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

    @Override
    public void updateEnemies() {
        texts.clear();
        for(int i = 0; i< Main.enemies.size(); i++) {
            Text t = new Text("* " + Main.enemies.get(i).getName());
            texts.add(t);
        }

        firstRow.getChildren().clear();
        secondRow.getChildren().clear();

        for (Text text : texts) {
            firstRow.getChildren().add(text);
        }
        configureText(45);

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectInteraction(Directions direction, Player player, int menu) {

        switch (direction) {
            case UP, LEFT -> {
                if(menu > 0) {
                    Globals.switchCurrentElementSound.play();
                    --menu;
                }
            }
            case DOWN, RIGHT -> {
                if(menu+1 < getTexts().size()) {
                    Globals.switchCurrentElementSound.play();
                    ++menu;
                }
            }
        }

        if(needsSelectedEnemy && !hasSelectedEnemy) {
            selectedEnemy = menu;
        }

        player.movePlayer(getTextX(menu, player.getWidth()), getTextY(menu, player.getHeight()));
    }
}