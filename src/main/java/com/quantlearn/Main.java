package com.quantlearn;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.quantlearn.backtest.BacktestEngine;
import com.quantlearn.backtest.BacktestResult;
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
        System.err.println("--- Backtesting engine initialising ---\n");

        String csvFilePath = "data/SPY.csv";
        String symbol = "SPY";
        double initialCash = 100000.0;

        IDataHandler dataHandler = new CsvDataHandler(csvFilePath, symbol);

        Scanner sc = new Scanner(System.in);
        int choice = -1;

        System.out.println("Choose a strategy:");
        System.out.println("1. Buy and Hold");
        System.out.println("2. SMA Cross (20, 50)");
        System.out.print("Enter choice (1 or 2): ");

        try {
            choice = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("\nInvalid input. Please enter a number (1 or 2).");
            System.exit(1);
        }

        System.out.print("\nFor Sharpe Ratio, ignore days with zero returns? (true/false): ");
        boolean ignoreZeroReturnDays = sc.nextBoolean();
        sc.close();

        IStrategy strategy;

        switch (choice) {
            case 1 -> strategy = new BuyAndHoldStrategy(symbol);
            case 2 -> strategy = new SmaCrossStrategy(symbol, 20, 50);
            default -> {
                System.out.println("\nInvalid choice. Please select 1 or 2.");
                System.exit(1);
                return;
            }
        }

        IPortfolio portfolio = new BasicPortfolio(initialCash, ignoreZeroReturnDays);
        IExecutionHandler executionHandler = new SimulatedExecutionHandler();

        BacktestEngine engine = new BacktestEngine(
            dataHandler,
            executionHandler,
            portfolio,
            strategy
        );

        BacktestResult result = engine.run();
        System.out.println(result);

        ChartGenerator.generateEquityCurve(result, "equity_curve.png");

        System.out.println("\n--- Backtest complete ---");
    }
}
