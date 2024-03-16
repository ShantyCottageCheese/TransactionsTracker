package tracker.transactionstracker.correlations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tracker.transactionstracker.databse.DatabaseService;
import tracker.transactionstracker.marketdata.BinanceService;
import tracker.transactionstracker.marketdata.CryptoPriceHistory;
import tracker.transactionstracker.model.TransactionEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static tracker.transactionstracker.correlations.Correlation.correlationCoefficient;
import static tracker.transactionstracker.correlations.TransactionMapper.convertEntityToBlockchainDataTransactionsMap;

@Service
@Slf4j
public class CorrelationService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yy");
    private final DatabaseService databaseService;
    private final BinanceService binanceService;

    public CorrelationService(DatabaseService databaseService, BinanceService binanceService) {
        this.databaseService = databaseService;
        this.binanceService = binanceService;
    }

    public Map<String, CorrelationData> getCorrelationForPeriods(int days) {
        Map<String, List<TransactionEntity>> transactions = databaseService.getTransactionsFromLastDays(days);
        Map<String, CryptoPriceHistory> marketDataMap = binanceService.getMarketData(days + 1);
        Map<String, BlockchainDataTransaction> blockchainDataTransactionMap = convertEntityToBlockchainDataTransactionsMap(transactions, marketDataMap);
        return calculateCorrelationForPeriods(blockchainDataTransactionMap);
    }


    public Map<String, CorrelationData> calculateCorrelationForPeriods(Map<String, BlockchainDataTransaction> blockchainDataTransactionMap) {
        Map<String, CorrelationData> correlationResults = new HashMap<>();
        int periodSize = 7;

        for (Map.Entry<String, BlockchainDataTransaction> entry : blockchainDataTransactionMap.entrySet()) {
            String blockchainName = entry.getKey();
            BlockchainDataTransaction dataTransaction = entry.getValue();
            CorrelationData correlationData = new CorrelationData();
            List<String> sortedDates = new ArrayList<>(dataTransaction.getBlockchainData().keySet());
            if (sortedDates.size() < periodSize)
                continue;
            sortedDates.sort(Comparator.comparing(s -> LocalDate.parse(s, FORMATTER)));

            for (int i = 0; i < sortedDates.size(); i += periodSize) {
                int remainingDays = sortedDates.size() - i;
                int currentPeriodSize = Math.min(periodSize, remainingDays);

                List<Double> transactionAmounts = new ArrayList<>();
                List<Double> prices = new ArrayList<>();

                String startDate = sortedDates.get(i);
                String endDate = sortedDates.get(i + currentPeriodSize - 1);

                for (int j = i; j < i + currentPeriodSize; j++) {
                    String date = sortedDates.get(j);
                    TransactionDto dto = dataTransaction.getBlockchainData().get(date);
                    if (dto != null) {
                        transactionAmounts.add(dto.getTwentyFourHourChange() == null ? 0.0 : dto.getTwentyFourHourChange().doubleValue());
                        prices.add(dto.getPrice().doubleValue());
                    }
                }

                if (!transactionAmounts.isEmpty() && transactionAmounts.size() == prices.size()) {
                    BigDecimal correlation = correlationCoefficient(transactionAmounts, prices);
                    String range = startDate + " - " + endDate;
                    correlationData.getDateRangeWithCorrelation().putIfAbsent(range, correlation);
                } else
                    correlationData.getDateRangeWithCorrelation().putIfAbsent("N/A", null);

                if (currentPeriodSize < periodSize)
                    break;
            }
            correlationResults.put(blockchainName, correlationData);
        }
        return correlationResults;
    }
}
