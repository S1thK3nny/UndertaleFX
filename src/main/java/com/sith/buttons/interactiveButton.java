package com.sith.buttons;

import com.sith.Main;
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

        options.setSpacing(Main.fb.getWidth()/2.5);
        firstRow.setSpacing(Main.fb.getStrokeWidth()/2);
        secondRow.setSpacing(Main.fb.getStrokeWidth()/2);
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
        options.setTranslateX(Main.fb.getX()*1.5 - Main.fb.getStrokeWidth());
        options.setTranslateY(Main.fb.getY() + t.getBoundsInLocal().getHeight()/2 - Main.fb.getStrokeWidth());
        options.setVisible(true);
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

    public ArrayList<Text> getTexts() {
        return texts;
    }

    public Text getText(int i) {
        return texts.get(i);
    }

    public double getTextX(int i, double playerWidth) {
        if(i>texts.size() || i<0) return 0;
        //I swear to god, this HAS TO BE 0!!! DO NOT CHANGE IT!
        return texts.get(0).getBoundsInLocal().getCenterX() + Main.fb.getX() + texts.get(i).getParent().getLayoutX() - playerWidth/2;
    }

    public double getTextY(int i, double playerHeight) {
        if(i>texts.size() || i<0) return 0;
        //IT TOOK ME HALF AN HOUR TO COME UP WITH THIS; DO NOT EVER MESS WITH THIS AGAIN
        //return Main.fb.getY() + texts.get(i).getLayoutY() + texts.get(i).getParent().getLayoutY() + texts.get(i).getParent().getBoundsInLocal().getCenterY();
        return Main.fb.getY() + texts.get(i).getLayoutY() - playerHeight/2;
    }
}
