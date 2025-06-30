package com.quantlearn.backtest.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Queue;

import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;

import com.quantlearn.backtest.IDataHandler;
import com.quantlearn.backtest.event.Event;
import com.quantlearn.backtest.event.MarketEvent;

public class CsvDataHandler implements IDataHandler {

    private final String symbol;
    private final BufferedReader reader;
    private String nextLine;


    public CsvDataHandler(String csvFilePath, String symbol) {
        this.symbol = symbol;
        try {
            this.reader = new BufferedReader(new FileReader(csvFilePath));
            this.reader.readLine();
            this.nextLine = this.reader.readLine();
        } catch (IOException e){
            throw new RuntimeException("failed to open csv file at path: "+csvFilePath, e);
        }
    }

    @Override 
    public boolean hasNext() {
        return this.nextLine != null;
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public void next(Queue<Event> eventQueue){
        if(!hasNext()) {
            return;
        }
        String currentLine = nextLine;
        try {
            this.nextLine = reader.readLine();
        } catch (IOException e) {
            //this.nextLine = null;
            throw new RuntimeException("failed to read data line from the CSV file", e);
        }

        String[] values = currentLine.split(",");

        try {
            if (values.length < 7) {
                System.err.println("Skipping malformed data line (not enough columns): " + currentLine);
                return;
            }
            LocalDateTime localDateTime  = LocalDateTime.parse(values[0] + "T16:00:00");
            ZoneId zoneId = ZoneId.of("America/New_York");
            ZonedDateTime dateTime = localDateTime.atZone(zoneId);

            @SuppressWarnings("UnnecessaryTemporaryOnConversionFromString")
            Double open = Double.parseDouble(values[1]);
            @SuppressWarnings("UnnecessaryTemporaryOnConversionFromString")
            Double high = Double.parseDouble(values[2]);
            @SuppressWarnings("UnnecessaryTemporaryOnConversionFromString")
            Double low = Double.parseDouble(values[3]);
            @SuppressWarnings("UnnecessaryTemporaryOnConversionFromString")
            Double close = Double.parseDouble(values[4]);
            @SuppressWarnings("UnnecessaryTemporaryOnConversionFromString")
            Double volume = Double.parseDouble(values[6]);

            Bar bar = new BaseBar(Duration.ofDays(1), dateTime, open, high, low, close, volume);
            MarketEvent event = new MarketEvent(dateTime.toInstant(), symbol, bar);
            eventQueue.add(event);

        } catch (Exception e) {
            System.err.println("parsing error on line:  " + currentLine);
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        if(this.reader!=null) {
            this.reader.close();
        }
    }
}
