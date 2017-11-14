package ru.gpanov;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

public class Charts {
    public static void main(String[] args) {
        int[] n = {8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40};
        int[] d = {4, 4,  4,  4,  5,  6,  6,  7,  8,  7,  8,  8,  8,  8,  8,  9,  10};
        double[] errs = {0.1, 0.01, 0.001};

        XYChart chart = new XYChartBuilder().width(800).height(600).title("title").xAxisTitle("n").yAxisTitle("err").build();

        for(double err: errs) {
            double[] x = new double[n.length];
            double[] y = new double[n.length];
            for (int i = 0; i < n.length; i++) {
                x[i] = n[i];
                y[i] = fu(n[i], d[i], err);
            }
            chart.addSeries(err + "", x, y);
        }
        chart.getStyler().setYAxisLogarithmic(true);
        new SwingWrapper(chart).displayChart();
    }

    private static double fu(double n, int d, double prob) {
        double fx = 0;
        for (int j = d  / 2; j <= n + 1; j++) {
            fx += Utils.binomialCoefficient(n, j) * Math.pow((1 - prob), n - j) * Math.pow(prob, j);
        }
        return fx;
    }
}