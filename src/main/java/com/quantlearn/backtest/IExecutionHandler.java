package com.quantlearn.backtest;

import java.util.Queue;

import com.quantlearn.backtest.event.Event;
import com.quantlearn.backtest.event.MarketEvent;
import com.quantlearn.backtest.event.OrderEvent;

public interface IExecutionHandler {
    void onOrderEvent(OrderEvent order, Queue<Event> eventQueue);

    void onMarketEvent(MarketEvent event);
}
