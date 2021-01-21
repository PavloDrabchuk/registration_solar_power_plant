package com.example.demo.model;

public enum Region {
   // Київська, ІваноФранківська, Львівська
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
