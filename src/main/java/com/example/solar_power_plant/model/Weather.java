package com.example.solar_power_plant.model;

public enum Weather {
    ClearSky(1D, "clear sky"),
    FewClouds(0.8D,"few clouds"), //60-80%
    ScatteredClouds(0.4D,"scattered clouds"), //30-40%
    BrokenClouds(0.3D,"broken clouds"), //20-30%
    OvercastClouds(0.2D,"overcast clouds"),
    ShowerRain(0.1D,"shower rain"),
    LightRain(0.2D,"light rain"),
    HeavyIntensityRain(0.1D,"heavy intensity rain"),
    Thunderstorm(0.01D,"thunderstorm"),
    Mist(0.05D,"mist"),
    Snow(0.15D,"snow"),
    Other(0.01D,"other");

    private final double coefficient;
    private final String description;

    Weather(double coefficient, String description) {
        this.coefficient = coefficient;
        this.description = description;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public String getDescription() {
        return description;
    }
}
