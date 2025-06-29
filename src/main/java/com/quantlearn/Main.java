package com.quantlearn;

import com.quantlearn.backtest.IDataHandler;
import com.quantlearn.backtest.IStrategy;
import com.quantlearn.backtest.data.CsvDataHandler;
import com.quantlearn.strategy.BuyAndHoldStrategy;

public class Main {
    public static void main(String[] args) {
        System.err.println("--- Backtesting engine initialising ---");
        String csvFilePath = "data/SPY.csv";
        String symbol = "SPY";
        double initialCash = 100000.0;

        IDataHandler dataHandler = new CsvDataHandler(csvFilePath, symbol);

        IStrategy strategy = new BuyAndHoldStrategy(symbol);

        //IPortfolio portfolio = new 
    }
}
