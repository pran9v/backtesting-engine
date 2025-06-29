package com.quantlearn.strategy;

import java.util.Queue;

import com.quantlearn.backtest.IStrategy;
import com.quantlearn.backtest.event.Event;
import com.quantlearn.backtest.event.MarketEvent;
import com.quantlearn.backtest.event.SignalEvent;
import com.quantlearn.data.OrderDirection;

public class BuyAndHoldStrategy implements IStrategy {
    private final String symbol;
    private boolean hasBought = false;

    public BuyAndHoldStrategy(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public void onMarketEvent(MarketEvent event, Queue<Event> eventQueue) {
        if(event.getSymbol().equals(symbol) && !hasBought) {
            SignalEvent signal = new SignalEvent(
                event.getTimestamp(),
                symbol,
                OrderDirection.BUY,
                1.0
            );

            eventQueue.add(signal); 

            this.hasBought = true;

            System.out.printf("%s | SIGNAL : Generated BUY signal for %s%n", event.getTimestamp(), this.symbol);
        }
    }
}
