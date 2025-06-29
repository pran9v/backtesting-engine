package com.quantlearn.backtest.event;

import java.time.Instant;

import com.quantlearn.data.OrderDirection;

public record FillEvent(
    Instant timestamp,
    String symbol,
    OrderDirection direction,
    double quantity,
    double price,
    double commission
) implements Event{
    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }
}
