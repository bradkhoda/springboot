package com.example.demo;

import com.example.demo.Coin;

public class Coins {
    // Class to hold multiple instances of Coins

    // Class Properties
    private final int id;
    private final Coin[] coinsArray;


    // Class Methods
    public Coins(int id, Coin[] coinsArray) {
        this.id = id;
        this.coinsArray = coinsArray;
    }

    // Getters

    public int getId() {
        return this.id;
    }
    public Coin[] getCoinsArray() {
        return this.coinsArray;
    }
}