package com.quantlearn.backtest;

import java.util.Queue;

import com.quantlearn.backtest.event.Event;
import com.quantlearn.backtest.event.MarketEvent;

public interface IStrategy {

    /**
    * why do we need 2 @param as event and eventQueue?
    * well, one way i thought of was to just pop the event here itself and not pass the "MarketEvent"
    * but, then i have to manually check whether the popped event is a "MarketEvent", "SignalEvent" or "FillEvent"
    * and then if it's not "MarketEvent", i had to push the event back causing unnecessary overhead
    */
    
    void onMarketEvent(MarketEvent event, Queue<Event> eventQueue);
}
