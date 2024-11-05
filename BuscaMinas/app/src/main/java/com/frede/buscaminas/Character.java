package com.frede.buscaminas;

public class Character {
    private String name;
    private int iconResource;

    public Character(String name, int iconResource) {
        this.name = name;
        this.iconResource = iconResource;
    }

    public String getName() {
        return name;
    }

    public int getIconResource() {
        return iconResource;
    }
}