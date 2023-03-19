package com.sith;

public class Item {
    private final String name;
    private final int hp;

    public Item(String name, int hp) {
        this.name = name;
        this.hp = hp;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public String toString() {
        return "Item name: " + name + ": restores " + hp + "Hp";
    }
}