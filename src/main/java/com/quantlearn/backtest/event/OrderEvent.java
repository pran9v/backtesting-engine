package com.quantlearn.backtest.event;

import java.time.Instant;

import com.quantlearn.data.OrderDirection;
import com.quantlearn.data.OrderType;

public record OrderEvent(
    Instant timestamp,
    String symbol,  
    OrderType type,
    OrderDirection direction,
    double quantity
) implements Event {
    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }
}
