package com.sith.buttons;

import com.sith.Globals;
import com.sith.Main;
import com.sith.Player;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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

        configureText(45);

        for (Text text : texts) {
            Rectangle health = new Rectangle(Main.fb.getWidth() / 7, text.getLayoutBounds().getHeight() / 2, Color.LIME);
            Rectangle lostHealth = new Rectangle(health.getWidth(), health.getHeight(), Color.RED);
            HBox healthBar = new HBox(lostHealth, health);
            healthBar.setSpacing(-health.getWidth());
            HBox.setHgrow(healthBar, Priority.ALWAYS);
            healthBar.setAlignment(Pos.CENTER_RIGHT);

            HBox hbox = new HBox(text, healthBar);
            hbox.setSpacing(text.getBaselineOffset());
            firstRow.getChildren().add(hbox);
        }

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

    //We have to override this function cause otherwise it won't work
    @Override
    public double getTextY(int i, double playerHeight) {
        if(i>texts.size() || i<0) return 0;
        return Main.fb.getY() + (texts.get(i).getParent().getLayoutY() + texts.get(i).getLayoutBounds().getHeight()/2);
    }

    @Override
    public void actionAfterEnemySelected() {
        super.actionAfterEnemySelected();
        options.setVisible(false);
        Main.bt.setVisible(true);
    }

    @Override
    public String interact() {
        Globals.sliceSound.play();
        return "* Oh no...what happened here?! " + option;
    }
}