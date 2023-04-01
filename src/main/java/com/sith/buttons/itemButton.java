package com.sith.buttons;

import com.sith.Item;
import com.sith.Main;
import com.sith.Player;
import com.sith.globals;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class itemButton extends interactiveButton {

    final ArrayList<Item> items = new ArrayList<>();
    final ArrayList<ArrayList<Item>> itemPages = new ArrayList<>();
    Player player;
    String pageText = "PAGE ";
    Text currentPageText = new Text(pageText);
    int currentPage = 0;
    int itemsPerPage = 4;
    VBox tempSpace = new VBox();

    public itemButton(Player player) {
        super(globals.itemButton, globals.itemButtonSelected);

        items.add(new Item("Butterscotch Pie", 99));
        items.add(new Item("Legendary Hero", 40));
        items.add(new Item("Mayonnaise", 27, "You devoured the Mayonnaise...?"));
        items.add(new Item("Banana", 10, "The length is perfectly to your liking."));
        items.add(new Item("Pipis", 5));
        items.add(new Item("Pipis", 5));
        items.add(new Item("Pipis", 5));
        items.add(new Item("Pipis", 5));
        items.add(new Item("Pipis", 5));
        items.add(new Item("Pipis", 5));

        currentPageText.setVisible(false);

        createItemPages();
        setVisibleItemPage(0);

        configureText(player.getWidth());
        configurePageText(player.getWidth(), currentPageText);
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

        //This takes care of removing the correct item depending on your currentPageText
        option += currentPage*itemsPerPage;

        //Need the substring to ignore the "* "
        String returnString;
        if(items.get(option).getHp() + player.getCurHealth() >= player.getMaxHealth()) {
            returnString = "* " + items.get(option).getItemDescription() + "\n* Your HP was maxed out.";
        }
        else {
            returnString = "* " + items.get(option).getItemDescription() + "\n* You recovered " + items.get(option).getHp() + "HP!";
        }
        player.healPlayer(items.get(option).getHp());
        removeItem(option);
        return returnString;
    }

    public void addToRow() {
        firstRow.getChildren().clear();
        secondRow.getChildren().clear();
        for (int i = 0; i < texts.size(); i++) {
            if(i>3) {
                tempSpace.getChildren().add(texts.get(i));
            }
            else {
                if (i % 2 == 0) {
                    firstRow.getChildren().add(texts.get(i));
                } else {
                    secondRow.getChildren().add((texts.get(i)));
                }
            }
        }

        if(items.size()>=5) {
            currentPageText.setTranslateY((Main.fb.getY() - Main.fb.getHeight()) / 2);
            currentPageText.setVisible(true);
            secondRow.getChildren().add(currentPageText);
        }
        else {
            currentPageText.setVisible(false);
            secondRow.getChildren().remove(currentPageText);
        }
    }

    public void removeItem(int option) {
        items.remove(items.get(option));
        texts.clear();

        createItemPages();
        setVisibleItemPage(0);

        //Alright, I swear to god, the configureText has to be here. I tried to move it to setVisibleItemPage(), but after some refactoring it gave me unexplainable errors. Just let it stay here.
        configureText(player.getWidth());

        addToRow();
    }

    public void createItemPages() {
        itemPages.clear();
        int numPages = (int) Math.ceil(items.size() / (double) itemsPerPage); //Determines the amount of pages that we need.
        for(int i=0; i<numPages; i++) {
            int start = i * itemsPerPage;
            int end = Math.min(start + itemsPerPage, items.size()); //If there are less than 4 items in one currentPageText, let's say 3, then it will pick the 3

            ArrayList<Item> page = new ArrayList<>(items.subList(start, end));
            itemPages.add(page);
        }
    }

    public void setVisibleItemPage(int page) {
        if(page>=itemPages.size() || page<0) return;
        for(Item item : itemPages.get(page)) {
            texts.add(new Text("* " + item.getName()));
        }
        setCurrentPage(page);
    }

    public void setCurrentPage(int page) {
        currentPage = page;
        currentPageText.setText(pageText + (currentPage+1));
    }

    public int getItemPagesSize() {
        return itemPages.size();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean noItemsLeft() {
        return items.size()<=0;
    }
}