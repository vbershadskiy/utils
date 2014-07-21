package com.vbersh.model;

import java.util.Observable;

public class StockObservable extends Observable {

    private Double price;
    private String symbol;

    public void setPrice(Double price) {
        this.price = price;
        setChanged();
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
        setChanged();
    }

    @Override
    public String toString() {
        return "StockObservable{" +
                "price=" + price +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
