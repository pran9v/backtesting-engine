package com.quantlearn.backtest;

public record BacktestResult(
        double initialCapital,
        double finalEquity,
        double totalProfit,
        double totalReturnPercentage,
        int numOfTrades
) {

    @Override
    public String toString() {
        return """
                -------------------------------------------
                |          Backtest Results               |
                -------------------------------------------
                | Initial Capital:       $%,.2f
                | Final Equity:          $%,.2f
                | Net Profit / Loss:     $%,.2f
                | Total Return:          %.2f%%
                | Number of Trades:      %d
                -------------------------------------------
                """.formatted(initialCapital, finalEquity, totalProfit, totalReturnPercentage, numOfTrades);
    }
}
