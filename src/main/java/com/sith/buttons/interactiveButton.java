package com.sith.buttons;

import com.sith.globals;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public abstract class interactiveButton extends Rectangle {
    private boolean selected = false;
    Image buttonSelected;
    Image buttonNotSelected;

    public interactiveButton(Image buttonNotSelected, Image buttonSelected) {
        this.buttonNotSelected = buttonNotSelected;
        this.buttonSelected = buttonSelected;
        this.setFill(new ImagePattern(buttonNotSelected));

        this.setWidth(buttonNotSelected.getWidth());
        this.setHeight(buttonNotSelected.getHeight());

        this.setScaleX(2.75);
        this.setScaleY(2.75);
    }

    public boolean isSelected() {
        return selected;
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
    }
}
