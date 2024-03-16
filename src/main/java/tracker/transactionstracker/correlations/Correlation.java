package tracker.transactionstracker.correlations;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Slf4j
public class Correlation {

    static BigDecimal correlationCoefficient(List<Double> coin1, List<Double> coin2) {
        double covarianceXY;
        double stdDivX;
        double stdDivY;
        covarianceXY = covariance(coin1, coin2);
        stdDivX = computeStdDeviation(coin1);
        stdDivY = computeStdDeviation(coin2);
        if (stdDivX == 0 || stdDivY == 0 || Double.isNaN(covarianceXY) || Double.isNaN(stdDivX) || Double.isNaN(stdDivY)) {
            return null; // The correlation cannot be computed
        }

        BigDecimal correlation = BigDecimal.valueOf(covarianceXY / (stdDivX * stdDivY));
        return correlation.setScale(2, RoundingMode.HALF_UP);
    }

    static double covariance(List<Double> x, List<Double> y) {

        double meanX = mean(x);
        double meanY = mean(y);
        int n = (int) Math.min(x.size(), (long) y.size());

        double sum = 0;

        if (n > 1) {
            sum = IntStream.range(0, n)
                    .mapToDouble(i -> {
                        double xi = x.get(i);
                        double yi = y.get(i);
                        return (xi - meanX) * (yi - meanY);
                    })
                    .sum() / (n - 1);
        }

        return sum;
    }

    static double computeStdDeviation(List<Double> data) {
        if (data.isEmpty())
            return 0;

        double mean = mean(data);
        AtomicReference<Double> sum = new AtomicReference<>((double) 0);
        AtomicInteger n = new AtomicInteger();

        data.stream().filter(Objects::nonNull).forEach(val ->{
            double dev = val - mean;
            sum.updateAndGet(v -> (v + dev * dev));
            n.getAndIncrement();
        });

        return Math.sqrt(sum.get() / (n.get() - 1));
    }

    static double mean(List<Double> data) {
        AtomicReference<Double> sum = new AtomicReference<>((double) 0);
        AtomicInteger n = new AtomicInteger();

        data.stream().filter(Objects::nonNull).forEach(val ->{
            sum.updateAndGet(v -> v + val);
            n.getAndIncrement();
        });

        return (n.get() > 0) ? sum.get() / n.get() : 0;
    }
}