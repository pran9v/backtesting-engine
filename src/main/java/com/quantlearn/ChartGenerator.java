package com.quantlearn;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

import com.quantlearn.backtest.BacktestResult;

public class ChartGenerator {

    public static void generateEquityCurve(BacktestResult result, String filePath) {
        if (result.equityCurveTimestamps().isEmpty() || result.equityCurveValues().isEmpty()) {
            System.out.println("No equity curve data to generate chart.");
            return;
        }

        List<Instant> timestamps = result.equityCurveTimestamps();
        List<Date> xData = new ArrayList<>();

        for (Instant timestamp : timestamps) {
            Date date = Date.from(timestamp);
            xData.add(date);
        }

        List<Double> yData = result.equityCurveValues();

        XYChart chart = new XYChartBuilder()
                .width(1200)
                .height(800)
                .title("Portfolio Equity Curve")
                .xAxisTitle("Date")
                .yAxisTitle("Portfolio Value ($)")
                .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setDatePattern("yyyy-MM-dd");
        chart.getStyler().setPlotMargin(0);
        chart.getStyler().setYAxisDecimalPattern("###,###,##0.00");

        chart.addSeries("Equity", xData, yData);

        try {
            BitmapEncoder.saveBitmap(chart, filePath, BitmapEncoder.BitmapFormat.PNG);
            System.out.println("\nEquity curve chart saved to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving chart: " + e.getMessage());
        }
    }
}