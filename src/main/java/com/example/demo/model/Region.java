package com.example.demo.model;

public enum Region {
    Kyiv("Київська"),
    IvanoFrankivsk("Івано-Франківська"),
    Lviv("Львівська");

    private final String name;

    Region(String name) {
        this.name=name;
    }

    public String getName() {
        return name;
    }
}
