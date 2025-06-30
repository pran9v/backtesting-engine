package com.quantlearn.backtest.portfolio;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.quantlearn.backtest.IPortfolio;
import com.quantlearn.backtest.event.Event;
import com.quantlearn.backtest.event.FillEvent;
import com.quantlearn.backtest.event.MarketEvent;
import com.quantlearn.backtest.event.OrderEvent;
import com.quantlearn.backtest.event.SignalEvent;
import com.quantlearn.data.OrderDirection;
import com.quantlearn.data.OrderType;


public class BasicPortfolio implements IPortfolio {
    private final double initialCash;
    private double currentCash;

    private final Map<String, Position> positions = new HashMap<>();
    private final Map<String, Double> marketValues = new HashMap<>();
    
    public BasicPortfolio(double initialCash) {
        this.initialCash = initialCash;
        this.currentCash = initialCash;
    }

    @Override
    public void onSignalEvent(SignalEvent signal, Queue<Event> eventQueue) {
        double quantity = 10;
        double currentPrice = getMarketValue(signal.getSymbol());
        System.out.printf("received signal for %s. Current market price is %.2f%n", 
        signal.getSymbol(), currentPrice);
        if(signal.direction() == OrderDirection.BUY) {
            double estimatedCost = quantity * getMarketValue(signal.symbol());
            if(currentCash >= estimatedCost && estimatedCost>0) {
                OrderEvent order = new OrderEvent(signal.timestamp(), signal.symbol(), OrderType.MARKET, OrderDirection.BUY, quantity);
                eventQueue.add(order);
            } else {
                System.out.println("not creating OrderEvent"); 
            }
        } else if(signal.direction() == OrderDirection.SELL) {
            Position currentPosition = positions.get(signal.symbol());
            if(currentPosition != null && currentPosition.getQuantity()>= quantity) {
                OrderEvent order = new OrderEvent(signal.timestamp(), signal.symbol(), OrderType.MARKET, OrderDirection.SELL, quantity);
                eventQueue.add(order);
            } else {
                System.out.println("not creating OrderEvent"); 
            }
        }
    }

    @Override
    public void onFillEvent(FillEvent fill) {
        double cost = fill.price() * fill.quantity();
        if(fill.direction() == OrderDirection.BUY) {
            currentCash -= (cost + fill.commission());
            Position position = positions.computeIfAbsent(fill.getSymbol(), p -> new Position(fill.getSymbol(), 0));
            position.updateQuantity(fill.quantity());
        } else if (fill.direction() == OrderDirection.SELL) {
            currentCash += (cost - fill.commission());
            Position position = positions.get(fill.symbol());
            if(position!=null) {
                position.updateQuantity(-fill.quantity());
            }
        }

        System.out.printf("%s | FILL: %s %d %s at %.2f. Cash: %.2f%n",
            fill.timestamp(), fill.direction(), (int)fill.quantity(), fill.getSymbol(), fill.price(), currentCash);
    }

    @Override
    public void updateMarketValue(MarketEvent event) {
        marketValues.put(event.symbol(), event.bar().getClosePrice().doubleValue());
    }

    public double getMarketValue(String symbol) {
        return marketValues.getOrDefault(symbol, 0.0);
    }

    public double getCurrentCash() {
        return currentCash;
    }

}
