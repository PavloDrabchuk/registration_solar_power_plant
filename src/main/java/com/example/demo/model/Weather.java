package com.example.demo.model;

public enum Weather {
    ClearSky(1D),
    FewClouds(0.8D), //60-80%
    ScatteredClouds(0.4D), //30-40%
    BrokenClouds(0.3D), //20-30%
    ShowerRain(0.1D),
    Rain(0.2D),
    Thunderstorm(0.01D),
    Mist(0.05D),
    Snow(0.4D);

    private final double coefficient;

    Weather(double coefficient) {
        this.coefficient=coefficient;
    }

    public double getCoefficient() {
        return coefficient;
    }
}
