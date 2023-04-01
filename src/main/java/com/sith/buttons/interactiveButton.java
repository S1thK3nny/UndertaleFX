package com.sith.buttons;

import com.sith.Main;
import com.sith.Player;
import com.sith.globals;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public abstract class interactiveButton extends Rectangle {
    public static boolean wentIntoButton = false;
    protected boolean selected = false;
    protected HBox options;
    protected VBox firstRow = new VBox();
    protected VBox secondRow = new VBox();
    Image buttonSelected;
    Image buttonNotSelected;
    protected ArrayList<Text> texts = new ArrayList<>();
    int option = 0;
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
            globals.switchCurrentElementSound.play();
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
        globals.buttonConfirmSound.play();
        options.setTranslateX(Main.fb.getX()*1.5 - Main.fb.getStrokeWidth());
        options.setTranslateY(Main.fb.getY() + t.getBoundsInLocal().getHeight()/2 - Main.fb.getStrokeWidth());
        options.setVisible(true);
        option = 0;
    }

    public String interact() {
        globals.buttonConfirmSound.play();
        return "* Oh no...what happened here?!";
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

    public void selectInteraction(String direction, Player player) {
        switch (direction) {
            case "up" -> {
                if(option >= 2) {
                    globals.switchCurrentElementSound.play();
                    option -= 2;
                }
                else if(option >= 1) {
                    globals.switchCurrentElementSound.play();
                    --option;
                }
            }
            case "left" -> {
                if(option > 0) {
                    --option;
                    globals.switchCurrentElementSound.play();
                }
            }
            case "down" -> {
                if(option+2 < getTexts().size()) {
                    globals.switchCurrentElementSound.play();
                    option +=2;
                }
                else if(option+1 < getTexts().size()) {
                    globals.switchCurrentElementSound.play();
                    ++option;
                }
            }
            case "right" -> {
                if(option < getTexts().size()-1 ) {
                    globals.switchCurrentElementSound.play();
                    ++option;
                }
            }
        }
        player.movePlayer(getTextX(option, player.getWidth()), getTextY(option, player.getHeight()));
    }
}
