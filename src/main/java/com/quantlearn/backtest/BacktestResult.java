package com.quantlearn.backtest;

import java.time.Instant;
import java.util.List;

public record BacktestResult(
        double initialCapital,
        double finalEquity,
        double totalProfit,
        double totalReturnPercentage,
        int numOfTrades,
        double maxDrawdownPercentage,
        double sharpeRatio,
        List<Instant> equityCurveTimestamps,
        List<Double> equityCurveValues,
        boolean sharpeIgnoresZeros
) {

    @Override
    public String toString() {
        String sharpeLabel = sharpeIgnoresZeros ? "Sharpe Ratio (Active Days):" : "Sharpe Ratio (Calendar Days):";
        
        return """
                -------------------------------------------
                |          Backtest Results               |
                -------------------------------------------
                | Initial Capital:       $%,.2f
                | Final Equity:          $%,.2f
                | Net Profit / Loss:     $%,.2f
                | Total Return:          %.2f%%
                | Max Drawdown:          %.2f%%
                | %-24s %.2f
                | Number of Trades:      %d
                -------------------------------------------
                """.formatted( initialCapital, finalEquity, totalProfit, totalReturnPercentage, maxDrawdownPercentage * 100, sharpeLabel, sharpeRatio, numOfTrades);
    }
}
