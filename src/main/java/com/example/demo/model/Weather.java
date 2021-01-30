package com.example.demo.model;

public enum Weather {
    ClearSky(1D),
    FewClouds(0.8D),
    ScatteredClouds(0.5D),
    BrokenClouds(0.3D),
    ShowerRain(0.1D),
    Rain(0.2D),
    Thunderstorm(0.01D),
    Snow(0.4D),
    Mist(0.05D);

    private final double coefficient;

    Weather(double coefficient) {
        this.coefficient=coefficient;
    }

    public double getCoefficient() {
        return coefficient;
    }
}
