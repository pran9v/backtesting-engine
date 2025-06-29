package com.quantlearn.backtest.event;

import java.time.Instant;

import org.ta4j.core.Bar;

public record MarketEvent(
    Instant timestamp,
    String symbol,
    Bar bar
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
