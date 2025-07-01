package com.quantlearn;

import java.util.Scanner;

import com.quantlearn.backtest.BacktestEngine;
import com.quantlearn.backtest.IDataHandler;
import com.quantlearn.backtest.IExecutionHandler;
import com.quantlearn.backtest.IPortfolio;
import com.quantlearn.backtest.IStrategy;
import com.quantlearn.backtest.data.CsvDataHandler;
import com.quantlearn.backtest.execution.SimulatedExecutionHandler;
import com.quantlearn.backtest.portfolio.BasicPortfolio;
import com.quantlearn.strategy.BuyAndHoldStrategy;
import com.quantlearn.strategy.SmaCrossStrategy;

public class Main {
    public static void main(String[] args) {
        System.err.println("--- Backtesting engine initialising ---");
        String csvFilePath = "data/SPY.csv";
        String symbol = "SPY";
        double initialCash = 100000.0;

        IDataHandler dataHandler = new CsvDataHandler(csvFilePath, symbol);

        Scanner sc = new Scanner(System.in);
        System.out.println("Choose a strategy:%n");
        System.out.println("1. BuyAndHold%n2. SmaCross(10, 30)");
        System.out.println("enter: ");
        int choice = sc.nextInt();

        IStrategy strategy = switch(choice) {
            case 1 -> new BuyAndHoldStrategy(symbol);
            case 2 -> new SmaCrossStrategy(symbol, 10, 30);
            default -> {
                System.out.println("Enter a valid choice.");
                System.exit(1);
                yield null; 
            }
        };

        IPortfolio portfolio = new BasicPortfolio(initialCash);
        
        IExecutionHandler executionHandler = new SimulatedExecutionHandler();

        BacktestEngine engine = new BacktestEngine(
            dataHandler,
            executionHandler,
            portfolio,
            strategy
        );

        engine.run();

        System.out.println("--- Backtest complete ---");
    }
}
