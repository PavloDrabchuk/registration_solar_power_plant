package com.example.solar_power_plant.model;

public enum Region {
    AutonomousRepublicOfCrimea("АР Крим"),
    Vinnytsia("Вінницька"),
    Volyn("Волинська"),
    Dnipropetrovsk("Дніпропетровська"),
    Donetsk("Донецька"),
    Zhytomyr("Житомирська"),
    Zakarpattia("Закарпатська"),
    Zaporizhzhia("Запорізька"),
    IvanoFrankivsk("Івано-Франківська"),
    Kyiv("Київська"),
    Kirovohrad("Кіровоградська"),
    Luhansk("Луганська"),
    Lviv("Львівська"),
    Mykolaiv("Миколаївська"),
    Odesa("Одеська"),
    Poltava("Полтавська"),
    Rivne("Рівненська"),
    Sumy("Сумська"),
    Ternopil("Тернопільська"),
    Kharkiv("Харківська"),
    Kherson("Херсонська"),
    Khmelnytskyi("Хмельницька"),
    Cherkasy("Черкаська"),
    Chernivtsi("Чернівецька"),
    Chernihiv("Чернігівська");

    private final String name;

    Region(String name) {
        this.name=name;
    }

    public String getName() {
        return name;
    }
}
