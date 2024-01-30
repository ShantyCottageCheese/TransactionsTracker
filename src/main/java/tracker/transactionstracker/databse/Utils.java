package tracker.transactionstracker.databse;

import tracker.transactionstracker.extractor.response.TransactionResponse;
import tracker.transactionstracker.model.TransactionEntity;
import tracker.transactionstracker.repository.TransactionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class Utils {
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");
    private static final int BATCH_SIZE = 100;
    static long now = System.currentTimeMillis();
    static long oneDayInMillis = 24 * 60 * 60 * 1000;
    static long startOfDay = now - (now % oneDayInMillis);

    static Long twentyFourHourChangeTransaction(TransactionResponse transactionResponse, TransactionRepository transactionRepository) {
        Optional<TransactionEntity> previousTransactionEntity = transactionRepository.findFirstByChainAndDateBeforeWithLimit(
                transactionResponse.getChain(),
                transactionResponse.getDate(),
                startOfDay
        );

        if (previousTransactionEntity.isPresent() && previousTransactionEntity.get().getAllTransactions() != null) {
            long result = transactionResponse.getAllTransactions() - previousTransactionEntity.get().getAllTransactions();
            return result >= 0 ? result : null;
        } else {
            return null;
        }
    }

    static String getPreviousDate() {
        LocalDate localDate = LocalDate.now(ZONE_ID).minusDays(1);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-dd-yyy");
        return localDateTime.format(formatter);
    }


    static long getPreviousTimestamp() {
        LocalDate localDate = LocalDate.now(ZONE_ID).minusDays(1);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        return localDateTime.atZone(ZONE_ID).toEpochSecond();
    }

    static <T> List<List<T>> getBatches(List<T> collection) {
        return IntStream.iterate(0, i -> i < collection.size(), i -> i + BATCH_SIZE)
                .mapToObj(i -> collection.subList(i, Math.min(i + BATCH_SIZE, collection.size())))
                .toList();
    }

    static long calculateFromDate(int days) {
        LocalDate date = LocalDate.now().minusDays(days + 1);
        return date.atStartOfDay(ZoneId.of("UTC")).toEpochSecond();
    }
}
