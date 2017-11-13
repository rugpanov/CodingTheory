package ru.gpanov;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Charts {
    public static void main(String[] args) {
        XYSeries series = new XYSeries("sin(a)");
        int[] n = {8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40};
        int[] d = {4, 4, 4, 4, 5, 6, 6, 7, 8, 7, 8, 8, 8, 8, 8, 9, 10};
        for (int i = 0; i < n.length; i++) {
            series.add(n[i], fu(n[i], d[i], 0.01));
            System.out.println(String.format("x = %d, y = %f", n[i], fu(n[i], d[i], 0.01)));
        }

        XYDataset xyDataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory
                .createXYLineChart("y = sin(x)", "x", "y",
                        xyDataset,
                        PlotOrientation.VERTICAL,
                        true, true, true);
        JFrame frame =
                new JFrame("MinimalStaticChart");
        // Помещаем график на фрейм
        frame.getContentPane()
                .add(new ChartPanel(chart));
        frame.setSize(400, 300);
        frame.show();
    }

    static double fu(int n, int d, double p) {
        int t = (d - 1) / 2;
        double secondArg = 0;
        for (int i = 0; i <= t; i++) {
            secondArg += Utils.binomialCoefficient(n, i) * Math.pow((1 - p), i) * Math.pow(p, n - i);
        }
        return 1 - secondArg;
    }


}
