package com.quantlearn.backtest;

import java.util.Queue;

import com.quantlearn.backtest.event.Event;
import com.quantlearn.backtest.event.FillEvent;
import com.quantlearn.backtest.event.MarketEvent;
import com.quantlearn.backtest.event.SignalEvent;

public interface IPortfolio {
    void onSignalEvent(SignalEvent event, Queue<Event> eventQueue);

    void onFillEvent(FillEvent event);

    void updateMarketValue(MarketEvent event);

    BacktestResult generateResult();
}
