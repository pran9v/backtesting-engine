package com.quantlearn.backtest.portfolio;

public class Position {
    private String symbol;
    private double quantity;

    public Position(String symbol, double quantity) {
        this.symbol = symbol;
        this.quantity = quantity;
    }

    public void updateQuantity(double change) {
        this.quantity += change;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getSymbol() {
        return symbol;
    }
}