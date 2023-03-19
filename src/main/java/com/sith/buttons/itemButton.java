package com.sith.buttons;

import com.sith.Item;
import com.sith.Player;
import com.sith.globals;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class itemButton extends interactiveButton {

    final ArrayList<Item> items = new ArrayList<>();
    Player player;

    public itemButton(Player player) {
        super(globals.itemButton, globals.itemButtonSelected);

        items.add(new Item("Pie", 99));
        items.add(new Item("Banana", 10));
        items.add(new Item("Banana2", 10));
        items.add(new Item("Banana3", 10));

        for(Item item : items) {
            texts.add(new Text("* " + item.getName()));
        }

        configureText(player.getWidth());
        this.player = player;
        addToRow();
    }

    @Override
    public void openButton() {
        super.openButton();
        System.out.println("Item button...");
        System.out.println("Still gotta add PAGE 2 stuff");
    }

    public String interact(int option) {
        super.interact(option);
        if(option>=texts.size() || option<0) return super.interact(option);
        //Need the substring to ignore the "* "
        String returnString = "* You tried to " + texts.get(option).getText().substring(2) + "\n* ...we will see how that turns out!";
        player.healPlayer(items.get(option).getHp());
        removeItem(option);
        return returnString;
    }

    public void addToRow() {
        for (int i=0; i<texts.size(); i++) {
            if(i%2==0) {
                firstRow.getChildren().add(texts.get(i));
            }
            else {
                secondRow.getChildren().add((texts.get(i)));
            }
        }
    }

    public void removeItem(int option) {
        items.remove(items.get(option));
        texts.remove(option);
        firstRow.getChildren().clear();
        secondRow.getChildren().clear();
        addToRow();
    }

    public boolean noItemsLeft() {
        return items.size()<=0;
    }
}