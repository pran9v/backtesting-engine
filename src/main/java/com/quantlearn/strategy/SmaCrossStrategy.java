package com.quantlearn.strategy;

import java.util.Queue;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.rules.CrossedDownIndicatorRule;

import com.quantlearn.backtest.IStrategy;
import com.quantlearn.backtest.event.Event;
import com.quantlearn.backtest.event.MarketEvent;
import com.quantlearn.backtest.event.SignalEvent;
import com.quantlearn.data.OrderDirection;

public class SmaCrossStrategy implements IStrategy {
    private final String symbol;
    private final int slowSmaPeriod;
    private final BarSeries series;
    private final SMAIndicator fastSma;
    private final SMAIndicator slowSma;
    private final Rule buyingRule;
    private final Rule sellingRule;

    public SmaCrossStrategy(String symbol, int fastSmaPeriod, int slowSmaPeriod) {
        this.symbol = symbol;
        this.slowSmaPeriod = slowSmaPeriod;
        this.series = new BaseBarSeriesBuilder().withName(symbol + "_Series").withNumTypeOf(DoubleNum.class).build();
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        this.fastSma = new SMAIndicator(closePrice, fastSmaPeriod);
        this.slowSma = new SMAIndicator(closePrice, slowSmaPeriod);
        // buy => when the fast sma crosses slow sma
        this.buyingRule = new CrossedDownIndicatorRule(fastSma, slowSma);
        // sell => when the slow sma crosses fast sma
        this.sellingRule = new CrossedDownIndicatorRule(slowSma, fastSma);
    }

    @Override
    public void onMarketEvent(MarketEvent event, Queue<Event> eventQueue) {
        if(!event.symbol().equals(this.symbol)) return; 

        series.addBar(event.bar());

        int currIndex = series.getEndIndex();
        if (currIndex < this.slowSmaPeriod - 1) return;

        if (buyingRule.isSatisfied(currIndex)) {
            System.out.printf("%s | signal: BUY for %s (fast sma crossed above slow sma)%n", event.timestamp(), symbol);
            SignalEvent signal = new SignalEvent(event.timestamp(), symbol, OrderDirection.BUY, 1.0);
            eventQueue.add(signal);
        } else if (sellingRule.isSatisfied(currIndex)) {
            System.out.printf("%s | signal: SELL for %s (fast sma crossed below slow sma)%n", event.timestamp(), symbol);
            SignalEvent signal = new SignalEvent(event.timestamp(), symbol, OrderDirection.SELL, 1.0);
            eventQueue.add(signal);
        }
    }
}
