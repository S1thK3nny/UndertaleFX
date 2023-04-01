package com.sith;

public class Item {
    private final String name;
    private final int hp;
    private final String itemDescription;

    public Item(String name, int hp) {
        this.name = name;
        this.hp = hp;
        this.itemDescription = "You ate the " + this.name + ".";
    }

    public Item(String name, int hp, String itemDescription) {
        this.name = name;
        this.hp = hp;
        this.itemDescription = itemDescription;
    }

    public String getName() {
        return name;
    }

    public String getItemDescription() {return itemDescription;}

    public int getHp() {
        return hp;
    }

    public String toString() {
        return "Item name: " + name + ": restores " + hp + "Hp";
    }
}