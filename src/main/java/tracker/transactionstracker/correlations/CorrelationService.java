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
import static tracker.transactionstracker.correlations.TransactionMapper.mapEntityToDto;

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

    public Map<String, Map<String, BigDecimal>> getCorrelationMap() {
        Map<String, List<TransactionEntity>> transactions = databaseService.getTransactionsFromLastDays(56);
        Map<String, Map<String, BigDecimal>> marketDataMap = binanceService.getMarketData(57);
        Map<String, Map<String, TransactionDto>> transactionDtoMap = mapEntityToDto(transactions, marketDataMap);
        return calculateCorrelationForPeriods(transactionDtoMap);

    }

    public Map<String, Map<String, BigDecimal>> calculateCorrelationForPeriods(Map<String, Map<String, TransactionDto>> transactionDtoMap) {
        Map<String, Map<String, BigDecimal>> correlationResults = new HashMap<>();

        for (String blockchain : transactionDtoMap.keySet()) {
            // Lista zawiera posortowane daty
            List<String> sortedDates = transactionDtoMap.get(blockchain).keySet()
                    .stream()
                    .sorted(Comparator.comparing(s -> LocalDate.parse(s, FORMATTER)))
                    .toList();

            int periodSize = 7; // Rozmiar okresu do przetwarzania danych
            for (int i = 0; i <= sortedDates.size() - periodSize; i += periodSize) {
                if ((sortedDates.size() - i) >= periodSize) {
                    List<Double> transactionAmounts = new ArrayList<>();
                    List<Double> prices = new ArrayList<>();

                    // Zakres dat do identyfikacji okresu
                    String startDate = sortedDates.get(i);
                    String endDate = sortedDates.get(i + periodSize - 1);

                    // Dodawanie danych do obliczeń korelacji
                    for (int j = i; j < i + periodSize; j++) {
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
                }
            }
        }

        return correlationResults;
    }
}
