package tracker.transactionstracker.correlations;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class Correlation {
//    static double correlationCoefficient(double[] coin1Array, double[] coin2Array) {
//        List<Double> coin1 = new LinkedList<>();
//        List<Double> coin2 = new LinkedList<>();
//        for (double c : coin1Array)
//            coin1.add(c);
//        for (double c : coin2Array)
//            coin2.add(c);
//
//        double covarianceXY, stdDivX, stdDivY, correlation;
//        covarianceXY = covariance(coin1, coin2);
//        stdDivX = computeStdDeviation(coin1);
//        stdDivY = computeStdDeviation(coin2);
//
//        return covarianceXY / (stdDivX * stdDivY);
//
//    }

    static BigDecimal correlationCoefficient(List<Double> coin1, List<Double> coin2) {
        double covarianceXY, stdDivX, stdDivY;
        covarianceXY = covariance(coin1, coin2);
        stdDivX = computeStdDeviation(coin1);
        stdDivY = computeStdDeviation(coin2);
        if (stdDivX == 0 || stdDivY == 0)
            return null;

        BigDecimal correlation = new BigDecimal(covarianceXY / (stdDivX * stdDivY));
        return correlation.setScale(2, RoundingMode.HALF_UP);

    }

    static double covariance(List<Double> x, List<Double> y) {

        double meanX = mean(x);
        double meanY = mean(y);
        double sum = 0;
        int n = 0;

        for (int i = 0; i < x.size(); i++) {
            Double xi = x.get(i);
            Double yi = y.get(i);

            if (xi != null && yi != null) {
                sum += (xi - meanX) * (yi - meanY);
                n++;
            }
        }

        return (n > 1) ? sum / (n - 1) : 0;
    }

    static double computeStdDeviation(List<Double> data) {
        if (data.isEmpty()) {
            return 0;
        }

        double mean = mean(data);
        double sum = 0;
        int n = 0;

        for (Double value : data) {
            if (value != null) {
                double dev = value - mean;
                sum += dev * dev;
                n++;
            }
        }

        return Math.sqrt(sum / (n - 1));
    }

    static double mean(List<Double> data) {
        double sum = 0;
        int n = 0;

        for (Double value : data) {
            if (value != null) {
                sum += value;
                n++;
            }
        }

        return (n > 0) ? sum / n : 0;
    }
}