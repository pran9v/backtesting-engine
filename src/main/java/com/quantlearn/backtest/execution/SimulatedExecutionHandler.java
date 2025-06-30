package com.quantlearn.backtest.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.ta4j.core.Bar;

import com.quantlearn.backtest.IExecutionHandler;
import com.quantlearn.backtest.event.Event;
import com.quantlearn.backtest.event.FillEvent;
import com.quantlearn.backtest.event.MarketEvent;
import com.quantlearn.backtest.event.OrderEvent;
import com.quantlearn.data.OrderDirection;

public class SimulatedExecutionHandler implements IExecutionHandler{

    private final Map<String, Bar> latestBars = new HashMap<>();

    @Override
    public void onMarketEvent(MarketEvent event) {
        latestBars.put(event.symbol(), event.bar());
    }

    @Override
    public void onOrderEvent(OrderEvent orderEvent, Queue<Event> eventQueue) {
        Bar latestBar = latestBars.get(orderEvent.getSymbol());

        if(latestBar==null) {
            System.err.printf("Warning: cannot execute order for %s. No market data available at %s.%n", orderEvent.getSymbol(), orderEvent.getTimestamp());
            return;
        }
        
        double fillPrice = calculateSlippage(orderEvent.direction(), latestBar);
        double commission = calculateCommission(orderEvent.quantity(), fillPrice);
        
        FillEvent fillEvent = new FillEvent(
            orderEvent.timestamp(),
            orderEvent.symbol(),
            orderEvent.direction(),
            orderEvent.quantity(),
            fillPrice,
            commission
        );

        eventQueue.add(fillEvent);
    }

    public double calculateSlippage(OrderDirection direction, Bar bar) {
        if(direction==OrderDirection.BUY) {
            return bar.getHighPrice().doubleValue();
        } else {
            return bar.getLowPrice().doubleValue();
        }
    }

    public double calculateCommission(double quantity, double price) {
        double tradeValue = quantity*price;
        double commission = Math.max(1.0, quantity*0.005);
        return Math.min(commission, tradeValue*0.01);
    }
}
