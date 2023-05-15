package com.example.demo;

public class Coin extends Object {
    // Coin class to hold each individual coin

    // Class Properties
    private final String currencyTag;
    private final String name;
    private final float priceAUD;

    // Class Methods
    public Coin(String currencyTag, String name, float priceAUD) {
        this.currencyTag = currencyTag;
        this.name = name;
        this.priceAUD = priceAUD;
    }

    // Getters
    public String getTag() {
        return this.currencyTag;
    }
    public String getName() {
        return this.name;
    }
    public float getPriceAUD() {
        return this.priceAUD;
    }
    
}