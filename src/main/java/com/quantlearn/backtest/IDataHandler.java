package com.quantlearn.backtest;

import java.io.IOException;
import java.util.Queue;

import com.quantlearn.backtest.event.Event;

public interface IDataHandler extends AutoCloseable {

    @Override
    void close() throws IOException;

    boolean hasNext();

    void next(Queue<Event> eventQueue);
}
