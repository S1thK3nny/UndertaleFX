package com.sith.buttons;

import com.sith.Main;
import com.sith.Player;
import com.sith.Globals;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public abstract class interactiveButton extends Rectangle {

    public enum Directions {
        UP,
        LEFT,
        DOWN,
        RIGHT
    }

    public static boolean hasSelectedEnemy = false;
    public boolean needsSelectedEnemy = false;
    protected boolean wantsToReturnTextAfterUsage = true;
    protected boolean selected = false;
    protected HBox options;
    protected VBox firstRow = new VBox();
    protected VBox secondRow = new VBox();
    Image buttonSelected;
    Image buttonNotSelected;
    protected ArrayList<Text> texts = new ArrayList<>();
    int option = 0;
    int selectedEnemy = 0;
    Text t;

    public interactiveButton(Image buttonNotSelected, Image buttonSelected) {
        this.buttonNotSelected = buttonNotSelected;
        this.buttonSelected = buttonSelected;
        this.setFill(new ImagePattern(buttonNotSelected));

        this.setWidth(buttonNotSelected.getWidth() * 2.75);
        this.setHeight(buttonNotSelected.getHeight() * 2.75);

        t = new Text("* Test");
        Main.configureText(t);

        options = new HBox();

        options.getChildren().addAll(firstRow, secondRow);
        options.setVisible(false);

        options.setSpacing(0);
        firstRow.setSpacing(Main.fb.getStrokeWidth()/2);
        firstRow.setMinWidth(Main.fb.getWidth()/2);
        secondRow.setSpacing(Main.fb.getStrokeWidth()/2);
        secondRow.setMinWidth(Main.fb.getWidth()/2);
    }

    public void setSelected(boolean selected) {
        if(selected) {
            setCurrentImage(buttonSelected);
            Globals.switchCurrentElementSound.play();
        }
        else {
            setCurrentImage(buttonNotSelected);
        }
        this.selected = selected;
    }

    public void select(interactiveButton[] buttons) {
        for (interactiveButton button : buttons) {
            button.setSelected(button == this);
        }
    }

    private void setCurrentImage(Image img) {
        this.setFill(new ImagePattern(img));
    }

    public void openButton() {
        Globals.buttonConfirmSound.play();
        options.setTranslateX(Main.fb.getX()*1.5 - Main.fb.getStrokeWidth());
        options.setTranslateY(Main.fb.getY() + t.getBoundsInLocal().getHeight()/2 - Main.fb.getStrokeWidth());
        options.setVisible(true);
        option = 0;
    }

    public String interact() {
        Globals.buttonConfirmSound.play();
        return "* Oh no...what happened here?! " + option;
    }

    public HBox getOptions() {
        return options;
    }

    public void hideOptions() {
        options.setVisible(false);
    }

    protected void configureText(double playerWidth) {
        for(Text text: texts) {
            Main.configureText(text);
            text.setTranslateX(text.getTranslateX() + playerWidth + Main.fb.getStrokeWidth());
        }
    }

    protected void configurePageText(double playerWidth, Text t) {
        Main.configureText(t);

        //Need this in order to align it with the second row
        Text temp = new Text("* ");
        Main.configureText(temp);

        t.setTranslateX(t.getTranslateX() + playerWidth + Main.fb.getStrokeWidth() + temp.getLayoutBounds().getWidth());
        t.setTranslateY(Main.fb.getHeight()-Main.fb.getHeight()/4);
    }

    public ArrayList<Text> getTexts() {
        return texts;
    }

    public double getTextX(int i, double playerWidth) {
        if(i>texts.size() || i<0) return 0;
        //I swear to god, this HAS TO BE 0!!! DO NOT CHANGE IT!
        return texts.get(0).getTranslateX()*2 + Main.fb.getX() + texts.get(i).getParent().getLayoutX() - playerWidth/2;
    }

    public double getTextY(int i, double playerHeight) {
        if(i>texts.size() || i<0) return 0;
        return Main.fb.getY() + texts.get(i).getLayoutY() - playerHeight/2;
    }

    public int getOption() {
        return option;
    }

    public int getSelectedEnemy() {
        return selectedEnemy;
    }

    public void getResetSelectedEnemy() {
        selectedEnemy = 0;
    }

    public boolean getWantsToReturnText() {
        return wantsToReturnTextAfterUsage;
    }

    public void selectInteraction(Directions direction, Player player, int menu) {

        switch (direction) {
            case UP -> {
                if(menu >= 2) {
                    Globals.switchCurrentElementSound.play();
                    menu -= 2;
                }
                else if(menu >= 1) {
                    Globals.switchCurrentElementSound.play();
                    --menu;
                }
            }
            case LEFT -> {
                if(menu > 0) {
                    Globals.switchCurrentElementSound.play();
                    --menu;
                }
            }
            case DOWN -> {
                if(menu+2 < getTexts().size()) {
                    Globals.switchCurrentElementSound.play();
                    menu +=2;
                }
                else if(menu+1 < getTexts().size()) {
                    Globals.switchCurrentElementSound.play();
                    ++menu;
                }
            }
            case RIGHT -> {
                if(menu < getTexts().size()-1 ) {
                    Globals.switchCurrentElementSound.play();
                    ++menu;
                }
            }
        }

        if(!needsSelectedEnemy || hasSelectedEnemy) {
            option = menu;
        }
        else {
            selectedEnemy = menu;
        }

        player.movePlayer(getTextX(menu, player.getWidth()), getTextY(menu, player.getHeight()));
    }

    public void updateEnemies() {
        texts.clear();
        for(int i=0; i< Main.enemies.size(); i++) {
            Text t = new Text("* " + Main.enemies.get(i).getName());
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
        configureText(45);

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void actionAfterEnemySelected() {
        option = 0;
        Globals.buttonConfirmSound.play();
    }
}