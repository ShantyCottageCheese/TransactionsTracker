package tracker.transactionstracker.databse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tracker.transactionstracker.extractor.Blockchain;
import tracker.transactionstracker.extractor.BlockchainExtractor;
import tracker.transactionstracker.extractor.response.TransactionResponse;
import tracker.transactionstracker.model.TransactionEntity;
import tracker.transactionstracker.repository.TransactionRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static tracker.transactionstracker.databse.Utils.*;

@Slf4j
@Service
public class DatabaseService {
    public static final int BATCH_SIZE = 100;
    private final BlockchainExtractor blockchainExtractor;
    private final TransactionRepository transactionRepository;
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");
   private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-dd-yyyy");


    public DatabaseService(BlockchainExtractor blockchainExtractor, TransactionRepository transactionRepository) {
        this.blockchainExtractor = blockchainExtractor;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void saveDataToDatabase() {
        saveTransactionsToDatabase(blockchainExtractor.extractBlockchainData());
    }

    private void saveTransactionsToDatabase(Map<Blockchain, List<TransactionResponse>> transactionResponseMap) {

        Set<String> nullTransactionsId = findNullTransactionIds();

        List<String> filteredTransactionsToUpdate = filterTransactionsToUpdate(transactionResponseMap, nullTransactionsId);

        List<TransactionEntity> transactionsToUpdate = updateTransactions(filteredTransactionsToUpdate, nullTransactionsId);

        List<String> idsToSave = transactionResponseMap.values().stream()
                .flatMap(List::stream)
                .map(TransactionResponse::getId).toList();

        List<String> existingEntities = transactionRepository.findAllById(idsToSave).stream()
                .map(TransactionEntity::getId)
                .toList();

        List<TransactionResponse> newTransactions = transactionResponseMap.values().stream()
                .flatMap(List::stream)
                .filter(transaction -> !existingEntities.contains(transaction.getId()))
                .toList();
        List<TransactionEntity> transactionsEntitiesToSave = createTransactionEntities(newTransactions);

        addBlockchainTransactionsWithoutData(transactionResponseMap, transactionsEntitiesToSave, nullTransactionsId, filteredTransactionsToUpdate);

        transactionsEntitiesToSave.addAll(transactionsToUpdate);
        transactionsEntitiesToSave.addAll(checkAndAddMissingRecords());

        log.info("Saving {} transactions to database", transactionsEntitiesToSave.size());
        if (!transactionsEntitiesToSave.isEmpty()) {
            getBatches(transactionsEntitiesToSave).forEach(transactionRepository::saveAll);
        }
    }

    private Set<String> findNullTransactionIds() {
        return transactionRepository.findAllWithNullChange()
                .stream()
                .map(TransactionEntity::getId)
                .collect(Collectors.toSet());
    }

    private List<String> filterTransactionsToUpdate(Map<Blockchain, List<TransactionResponse>> transactionResponseMap, Set<String> nullTransactionsId) {
        return transactionResponseMap.values().stream()
                .flatMap(List::stream)
                .map(TransactionResponse::getId)
                .filter(nullTransactionsId::contains)
                .toList();
    }

    private List<TransactionEntity> updateTransactions(List<String> filteredTransactionsToUpdate, Set<String> nullTransactionsId) {
        List<TransactionEntity> transactionsToUpdate = transactionRepository.findAllById(filteredTransactionsToUpdate);
        for (TransactionEntity transaction : transactionsToUpdate) {
            transaction.setTwentyFourHourChange(transaction.getTwentyFourHourChange());
            transaction.setAllTransactions(transaction.getAllTransactions());
            nullTransactionsId.remove(transaction.getId());
        }
        return transactionsToUpdate;
    }

    private List<TransactionEntity> createTransactionEntities(List<TransactionResponse> newTransactions) {
        return newTransactions.stream()
                .map(transaction -> TransactionEntity.builder()
                        .id(transaction.getId())
                        .chain(transaction.getChain())
                        .date(transaction.getDate())
                        .twentyFourHourChange(transaction.getChain().equals("Solana") || transaction.getChain().equals("Cardano") ? twentyFourHourChangeTransaction(transaction, transactionRepository) : transaction.getTwentyFourHourChange())
                        .allTransactions(transaction.getAllTransactions())
                        .build())
                .collect(Collectors.toList());
    }

    private void addBlockchainTransactionsWithoutData(Map<Blockchain, List<TransactionResponse>> transactionResponseMap, List<TransactionEntity> transactionsEntitiesToSave, Set<String> nullTransactionsId, List<String> existingEntities) {
        transactionResponseMap.forEach((blockchain, transactionResponses) -> {
            String previousDate = blockchain.getName() + "-" + getPreviousDate();
            if (transactionResponses.isEmpty() && !nullTransactionsId.contains(previousDate) && !existingEntities.contains(previousDate)) {
                transactionsEntitiesToSave.add(TransactionEntity.builder()
                        .id(previousDate)
                        .chain(blockchain.getName())
                        .date(getPreviousTimestamp())
                        .twentyFourHourChange(null)
                        .allTransactions(null)
                        .build());
            }
        });
    }

    private List<TransactionEntity> checkAndAddMissingRecords() {
        List<TransactionEntity> missingRecords = new ArrayList<>();
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusDays(6);

        List<TransactionEntity> existingTransactions = transactionRepository
                .findAllByDateBetween(startDate.atStartOfDay(ZONE_ID).toEpochSecond(), endDate.atStartOfDay(ZONE_ID).toEpochSecond());

        Map<String, Map<LocalDate, TransactionEntity>> transactionsGroupedByChainAndDate = existingTransactions.stream()
                .collect(Collectors.groupingBy(TransactionEntity::getChain,
                        Collectors.toMap(
                                transaction -> Instant.ofEpochSecond(transaction.getDate()).atZone(ZONE_ID).toLocalDate(),
                                Function.identity()
                        )));

        transactionsGroupedByChainAndDate.forEach((chain, transactionsByDate) -> {
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                if (!transactionsByDate.containsKey(date)) {
                    TransactionEntity missingTransaction = createMissingTransactionEntity(chain, date);
                    missingRecords.add(missingTransaction);
                }
            }
        });
        return missingRecords;
    }

    private TransactionEntity createMissingTransactionEntity(String chain, LocalDate date) {
        return TransactionEntity.builder()
                .id(chain + "-" + date.format(formatter))
                .chain(chain)
                .date(date.atStartOfDay(ZONE_ID).toEpochSecond())
                .twentyFourHourChange(null)
                .allTransactions(null)
                .build();
    }

}
