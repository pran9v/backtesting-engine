package com.quantlearn.backtest.portfolio;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.quantlearn.backtest.BacktestResult;
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
    private int tradeCount = 0;
    private final boolean ignoreZeroReturnDays;

    private final Map<String, Position> positions = new HashMap<>();
    private final Map<String, Double> marketValues = new HashMap<>();

    private final List<Instant> equityTimeStamps = new ArrayList<>();
    private final List<Double> equityCurve = new ArrayList<>();

    private record FilledEquityCurve(List<Instant> timestamps, List<Double> values) {}

    public BasicPortfolio(double initialCash, boolean ignoreZeroReturnDays) {
        this.initialCash = initialCash;
        this.currentCash = initialCash;
        this.ignoreZeroReturnDays = ignoreZeroReturnDays;
    }

    @Override
    public void onSignalEvent(SignalEvent signal, Queue<Event> eventQueue) {
        double quantity = 10;
        double currentPrice = getMarketValue(signal.getSymbol());
        System.out.printf("received signal for %s. Current market price is %.2f%n",
                signal.getSymbol(), currentPrice);
        if (signal.direction() == OrderDirection.BUY) {
            double estimatedCost = quantity * getMarketValue(signal.symbol());
            if (currentCash >= estimatedCost && estimatedCost > 0) {
                OrderEvent order = new OrderEvent(signal.timestamp(), signal.symbol(), OrderType.MARKET,
                        OrderDirection.BUY, quantity);
                eventQueue.add(order);
            } else {
                System.out.println("not creating OrderEvent");
            }
        } else if (signal.direction() == OrderDirection.SELL) {
            Position currentPosition = positions.get(signal.symbol());
            if (currentPosition != null && currentPosition.getQuantity() >= quantity) {
                OrderEvent order = new OrderEvent(signal.timestamp(), signal.symbol(), OrderType.MARKET,
                        OrderDirection.SELL, quantity);
                eventQueue.add(order);
            } else {
                System.out.println("not creating OrderEvent");
            }
        }
    }

    @Override
    public void onFillEvent(FillEvent fill) {
        tradeCount++;
        double cost = fill.price() * fill.quantity();
        if (fill.direction() == OrderDirection.BUY) {
            currentCash -= (cost + fill.commission());
            Position position = positions.get(fill.getSymbol());
            if (position == null) {
                position = new Position(fill.getSymbol(), fill.quantity());
                positions.put(fill.getSymbol(), position);
            } else {
                position.updateQuantity(fill.quantity());
            }
        } else if (fill.direction() == OrderDirection.SELL) {
            currentCash += (cost - fill.commission());
            Position position = positions.get(fill.symbol());
            if (position != null) {
                position.updateQuantity(-fill.quantity());
            }
        }

        System.out.printf("%s | FILL: %s %d %s at %.2f. Cash: %.2f%n",
                fill.timestamp(), fill.direction(), (int) fill.quantity(), fill.getSymbol(), fill.price(), currentCash);
    }

    @Override
    public void updateMarketValue(MarketEvent event) {
        marketValues.put(event.symbol(), event.bar().getClosePrice().doubleValue());
    }

    @Override
    public BacktestResult generateResult() {
        FilledEquityCurve filledCurve = createFilledEquityCurve();
        double maxDrawdown = calMaxDrawdown(filledCurve.values);
        double sharpeRatio = calSharpeRatio(filledCurve.values);

        double finalEquity = filledCurve.values.isEmpty() ? initialCash : filledCurve.values.get(filledCurve.values.size() - 1);
        double totalProfit = finalEquity - initialCash;
        double totalReturn = (initialCash == 0) ? 0 : totalProfit / initialCash;

        return new BacktestResult(
                this.initialCash, finalEquity, totalProfit, totalReturn * 100,
                this.tradeCount, maxDrawdown, sharpeRatio,
                filledCurve.timestamps, filledCurve.values, this.ignoreZeroReturnDays
        );
    }

    private double calSharpeRatio(List<Double> values) {
        if(values.size()<2) return 0.0; 

        ArrayList<Double> allDailyReturns = new ArrayList<>();
        for(int i=1; i<values.size(); i++) {
            double prev = values.get(i-1);
            allDailyReturns.add(prev==0 ? 0.0 : (values.get(i)/prev)-1);
        }

        List<Double> returnsForCalc = new ArrayList<>();
        double annulizationFactor;

        if(this.ignoreZeroReturnDays) {
            for (double r : allDailyReturns) {
                if (Math.abs(r) > 1e-9) {
                    returnsForCalc.add(r);
                }
            }
            annulizationFactor = 252.0;
        } else {
            returnsForCalc = allDailyReturns;
            annulizationFactor = 365.0;
        }
        double annualRiskFreeRate = 0.044;
        double dailyRiskFreeRate = annualRiskFreeRate / annulizationFactor;

        if (returnsForCalc.size() < 2) return 0.0;
        double sum = 0.0;
        for (double r : returnsForCalc) {
            sum+=r;
        }
        double mean = sum/returnsForCalc.size();

        double squaredDiff = 0.0;
        for(double r : returnsForCalc) {
            squaredDiff += Math.pow(r-mean, 2);
        }
        double sd = Math.sqrt(squaredDiff/returnsForCalc.size());
        
        if(sd == 0) return 0.0;

        return ((mean-dailyRiskFreeRate)/sd) * Math.sqrt(annulizationFactor);
    }

    private double calMaxDrawdown(List<Double> values) {
        if(values.size()==0) return 0.0;
        double maxDrawdown = 0.0, peak = -Double.MAX_VALUE;
        for(double equity : values) {
            if (equity > peak) peak = equity;
            if (peak > 0) {
                double drawDown = (peak-equity)/peak;
                maxDrawdown = Math.max(maxDrawdown, drawDown);
            }
        }
        return maxDrawdown;
    }

    private FilledEquityCurve createFilledEquityCurve() {
        if(this.equityTimeStamps.isEmpty()) return new FilledEquityCurve(new ArrayList<>(), new ArrayList<>());
        List<Instant> filledTimestamps = new ArrayList<>();
        List<Double> filledValues = new ArrayList<>();
        ZoneId zone = ZoneId.of("America/New_York");
        filledTimestamps.add(this.equityTimeStamps.get(0));
        filledValues.add(this.equityCurve.get(0));
        for (int i=1; i<this.equityTimeStamps.size(); i++) {
            LocalDate prevDate = this.equityTimeStamps.get(i-1).atZone(zone).toLocalDate();
            double prevValue = this.equityCurve.get(i-1);
            LocalDate currDate = this.equityTimeStamps.get(i).atZone(zone).toLocalDate();
            long daysBetween = ChronoUnit.DAYS.between(prevDate, currDate);
            if(daysBetween>1) {
                for(int j=1; j<daysBetween; j++) {
                    LocalDate dateToFill = prevDate.plusDays(j);
                    filledTimestamps.add(dateToFill.atStartOfDay(zone).toInstant());
                    filledValues.add(prevValue);
                }
            }
            filledTimestamps.add(this.equityTimeStamps.get(i));
            filledValues.add(this.equityCurve.get(i));
        }
        return new FilledEquityCurve(filledTimestamps, filledValues);
    } 

    @Override
    public void recordCurrentEquity(Instant timestamp) {
        this.equityTimeStamps.add(timestamp);
        this.equityCurve.add(calculateTotalEquity());
    }

    private double calculateTotalEquity() {
        double positionValue = 0.0;
        for (Position position : positions.values()) {
            double currentPrice = marketValues.get(position.getSymbol());
            positionValue += position.getQuantity() * currentPrice;
        }
        return this.currentCash + positionValue;
    }

    public double getMarketValue(String symbol) {
        return marketValues.getOrDefault(symbol, 0.0);
    }

    public double getCurrentCash() {
        return currentCash;
    }

}
