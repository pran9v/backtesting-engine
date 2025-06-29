package com.quantlearn.backtest.event;

import java.time.Instant;

public interface Event {
    Instant getTimestamp();
    String getSymbol();
}
