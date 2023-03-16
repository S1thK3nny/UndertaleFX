package com.sith.buttons;

import com.sith.Main;
import com.sith.globals;
import javafx.geometry.Pos;
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
    protected VBox options;
    protected HBox firstRow = new HBox();
    protected HBox secondRow = new HBox();
    Image buttonSelected;
    Image buttonNotSelected;
    protected ArrayList<Text> texts = new ArrayList<>();

    public interactiveButton(Image buttonNotSelected, Image buttonSelected) {
        this.buttonNotSelected = buttonNotSelected;
        this.buttonSelected = buttonSelected;
        this.setFill(new ImagePattern(buttonNotSelected));

        this.setWidth(buttonNotSelected.getWidth());
        this.setHeight(buttonNotSelected.getHeight());

        this.setScaleX(2.75);
        this.setScaleY(2.75);

        options = new VBox();

        firstRow.setAlignment(Pos.BASELINE_LEFT);
        secondRow.setAlignment(Pos.BASELINE_LEFT);

        options.setAlignment(Pos.BASELINE_LEFT);
        options.getChildren().addAll(firstRow, secondRow);
        options.setVisible(false);

        options.setSpacing(Main.fb.getHeight()/6);
        firstRow.setSpacing(0);
        secondRow.setSpacing(0);
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

    public void interact() {
        globals.buttonConfirmSound.play();
        options.setLayoutX(Main.fb.getX() + Main.fb.getWidth()/2 - options.getWidth()/2);
        options.setLayoutY(Main.fb.getY() + Main.fb.getHeight()/8);
        options.setVisible(true);
    }

    public VBox getOptions() {
        return options;
    }

    public void hideOptions() {
        options.setVisible(false);
    }

    protected void configureText(double playerWidth) {
        int id = 1;
        playerWidth *= 1.5;
        for(Text text: texts) {
            Main.configureText(text);
            text.setWrappingWidth(125);
            if(id%2==0) {
                text.setTranslateX(text.getTranslateX() - Main.fb.getWidth()/2 + Main.fb.getX() + text.getBoundsInLocal().getCenterX() + Main.fb.getWidth()*1/3 + playerWidth);
            }
            else {
                text.setTranslateX(text.getTranslateX() - Main.fb.getWidth()/2 + Main.fb.getX() + text.getBoundsInLocal().getCenterX() + playerWidth);
            }
            ++id;
        }
    }

    public ArrayList<Text> getTexts() {
        return texts;
    }

    public Text getText(int i) {
        return texts.get(i);
    }

    public double getTextX(int i) {
        if(i>texts.size() || i<0) return 0;
        if((i+1)%2==0) return Main.fb.getX() + texts.get(i).getBoundsInLocal().getCenterX() + Main.fb.getWidth()*1/3 + texts.get(i).getLayoutX();
        return Main.fb.getX() + texts.get(i).getBoundsInLocal().getCenterX();
    }

    public double getTextY(int i) {
        if(i>texts.size() || i<0) return 0;
        //IT TOOK ME HALF AN HOUR TO COME UP WITH THIS; DO NOT EVER MESS WITH THIS AGAIN
        return Main.fb.getY() + texts.get(i).getLayoutY() + texts.get(i).getParent().getLayoutY() + texts.get(i).getParent().getBoundsInLocal().getCenterY();
    }
}
