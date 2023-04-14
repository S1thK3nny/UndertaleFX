package com.sith.buttons;

import com.sith.Item;
import com.sith.Player;
import com.sith.Globals;
import javafx.application.Platform;
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

    public itemButton(Player player) {
        super(Globals.itemButton, Globals.itemButtonSelected);
        needsSelectedEnemy = false;

        items.add(new Item("Butterscotch Pie", 99));
        items.add(new Item("Legendary Hero", 40));
        items.add(new Item("Mayonnaise", 27, "You devoured the Mayonnaise...?"));
        items.add(new Item("Banana", 10, "The length is perfectly to your liking."));
        Item pipis = new Item("Pipis", 5);
        items.add(pipis);
        items.add(pipis);
        items.add(pipis);
        items.add(pipis);
        items.add(pipis);
        items.add(pipis);

        configurePageText(player.getWidth(), currentPageText);

        this.player = player;
    }

    @Override
    public void openButton() {
        super.openButton();
        switchItemPage(0);
    }

    public String interact() {
        super.interact();
        if(option>=texts.size() || option<0) return super.interact();

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
            if (i % 2 == 0) {
                firstRow.getChildren().add(texts.get(i));
            } else {
                secondRow.getChildren().add((texts.get(i)));
            }
        }

        if(items.size()>=5) {
            //currentPageText.setTranslateY((Main.fb.getY() - Main.fb.getHeight()) / 2);
            currentPageText.setVisible(true);
            secondRow.getChildren().add(currentPageText);
            currentPageText.setManaged(false);
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

    public void switchItemPage(int i) {
        texts.clear();
        createItemPages();
        setVisibleItemPage(i);
        configureText(player.getWidth());
        addToRow();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public boolean noItemsLeft() {
        return items.size()<=0;
    }



    @Override
    public void selectInteraction(Directions direction, Player player, int i) {
        switch (direction) {
            case UP -> {
                if(option >= 2) {
                    Globals.switchCurrentElementSound.play();
                    option -= 2;
                }
                else if(option >= 1) {
                    Globals.switchCurrentElementSound.play();
                    --option;
                }
            }
            case LEFT -> {
                if(currentPage>0 && (option == 0 || option == 2)) {
                    if(option == 2 && itemPages.get(currentPage-1).size()<2) {
                        option = 3;
                    }
                    else {
                        ++option;
                    }
                    Globals.switchCurrentElementSound.play();
                    switchItemPage(currentPage-1);
                }
                else if(option > 0) {
                    Globals.switchCurrentElementSound.play();
                    --option;
                }
            }
            case DOWN -> {
                if(option+2 < getTexts().size()) {
                    Globals.switchCurrentElementSound.play();
                    option +=2;
                }
                else if(option+1 < getTexts().size()) {
                    Globals.switchCurrentElementSound.play();
                    ++option;
                }
            }
            case RIGHT -> {
                if(currentPage<itemPages.size()-1 && (option == 1 || option == 3)) {
                    if(option == 3 && itemPages.get(currentPage+1).size()<3) {
                        option = 0;
                    }
                    else {
                        --option;
                    }
                    Globals.switchCurrentElementSound.play();
                    switchItemPage(currentPage+1);
                }
                else if(option < getTexts().size()-1) {
                    Globals.switchCurrentElementSound.play();
                    ++option;
                }
            }
        }

        //This has to be here, otherwise the Y position will be messed up.
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> player.movePlayer(getTextX(option, player.getWidth()), getTextY(option, player.getHeight())));
    }
}