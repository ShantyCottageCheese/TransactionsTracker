package tracker.transactionstracker.correlations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tracker.transactionstracker.databse.DatabaseService;
import tracker.transactionstracker.marketdata.BinanceService;
import tracker.transactionstracker.model.TransactionEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static tracker.transactionstracker.correlations.Correlation.correlationCoefficient;
import static tracker.transactionstracker.correlations.TransactionMapper.convertEntityToDto;

@Service
@Slf4j
public class CorrelationService {
    private final DatabaseService databaseService;
    private final BinanceService binanceService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yy");

    public CorrelationService(DatabaseService databaseService, BinanceService binanceService) {
        this.databaseService = databaseService;
        this.binanceService = binanceService;
    }

    public Map<String, Map<String, BigDecimal>> getCorrelationForPeriods(int days) {
        Map<String, List<TransactionEntity>> transactions = databaseService.getTransactionsFromLastDays(days);
        Map<String, Map<String, BigDecimal>> marketDataMap = binanceService.getMarketData(days + 1);
        Map<String, Map<String, TransactionDto>> transactionDtoMap = convertEntityToDto(transactions, marketDataMap);
        return calculateCorrelationForPeriods(transactionDtoMap);

    }

    public Map<String, Map<String, BigDecimal>> calculateCorrelationForPeriods(Map<String, Map<String, TransactionDto>> transactionDtoMap) {
        Map<String, Map<String, BigDecimal>> correlationResults = new HashMap<>();

        for (String blockchain : transactionDtoMap.keySet()) {
            List<String> sortedDates = transactionDtoMap.get(blockchain).keySet()
                    .stream()
                    .sorted(Comparator.comparing(s -> LocalDate.parse(s, FORMATTER)))
                    .toList();

            int periodSize = 7;
            for (int i = 0; i < sortedDates.size(); i += periodSize) {
                int remainingDays = sortedDates.size() - i;
                int currentPeriodSize = Math.min(periodSize, remainingDays);

                List<Double> transactionAmounts = new ArrayList<>();
                List<Double> prices = new ArrayList<>();

                String startDate = sortedDates.get(i);
                String endDate = sortedDates.get(i + currentPeriodSize - 1);

                for (int j = i; j < i + currentPeriodSize; j++) {
                    String date = sortedDates.get(j);
                    TransactionDto dto = transactionDtoMap.get(blockchain).get(date);
                    if (dto != null) {
                        transactionAmounts.add(dto.getTwentyFourHourChange() == null ? 0.0 : dto.getTwentyFourHourChange().doubleValue());
                        prices.add(dto.getPrice().doubleValue());
                    }
                }

                if (!transactionAmounts.isEmpty() && transactionAmounts.size() == prices.size()) {
                    BigDecimal correlation = correlationCoefficient(transactionAmounts, prices);
                    String range = startDate + " - " + endDate;
                    correlationResults.computeIfAbsent(blockchain, k -> new HashMap<>()).put(range, correlation);
                } else {
                    correlationResults.computeIfAbsent(blockchain, k -> new HashMap<>()).put("N/A", null);
                }
                // Przerywamy pętlę, gdy okres jest krótszy niż pełne 7 dni, ponieważ nie będzie już więcej okresów do obliczeń
                if (currentPeriodSize < periodSize)
                    break;

            }
        }

        return correlationResults;
    }
}
