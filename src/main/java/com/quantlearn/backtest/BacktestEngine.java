package com.quantlearn.backtest;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.quantlearn.backtest.event.Event;
import com.quantlearn.backtest.event.FillEvent;
import com.quantlearn.backtest.event.MarketEvent;
import com.quantlearn.backtest.event.OrderEvent;
import com.quantlearn.backtest.event.SignalEvent;

public class BacktestEngine {

    private final IDataHandler dataHandler;
    private final IExecutionHandler executionHandler;
    private final IPortfolio portfolio;
    private final IStrategy strategy;
    private final Queue<Event> eventQueue = new ConcurrentLinkedQueue<>();


    public BacktestEngine(IDataHandler dataHandler, IExecutionHandler executionHandler, IPortfolio portfolio, IStrategy strategy) {
        this.dataHandler = dataHandler;
        this.executionHandler = executionHandler;
        this.portfolio = portfolio;
        this.strategy = strategy;
    }

    public void run() {
        System.out.println("Starting Backtest...");
        try(IDataHandler dataHandler = this.dataHandler) {
            while(dataHandler.hasNext()) {
                dataHandler.next(eventQueue);
                while(!eventQueue.isEmpty()) {
                    Event event = eventQueue.poll();
                    processEvent(event);
                }
            }
        } catch(IOException e) {
            System.out.println("error during data handling");
        }
    }

    public void processEvent(Event event) {
        if(event instanceof MarketEvent me) {
            strategy.onMarketEvent(me, eventQueue);
            portfolio.updateMarketValue(me);
            executionHandler.onMarketEvent(me);
        } else if (event instanceof SignalEvent se) {
            portfolio.onSignalEvent(se, eventQueue);
        } else if(event instanceof OrderEvent oe) {
            executionHandler.onOrderEvent(oe, eventQueue);
        } else if(event instanceof FillEvent fe) {
            portfolio.onFillEvent(fe);
        }
    }
    
}
