package tracker.transactionstracker.databse;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tracker.transactionstracker.extractor.Blockchain;
import tracker.transactionstracker.extractor.BlockchainExtractor;
import tracker.transactionstracker.extractor.response.TransactionResponse;
import tracker.transactionstracker.model.TransactionEntity;
import tracker.transactionstracker.repository.TransactionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class DatabaseService {
    public static final int BATCH_SIZE = 100;
    private final BlockchainExtractor blockchainExtractor;
    private final TransactionRepository transactionRepository;
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");


    public DatabaseService(BlockchainExtractor blockchainExtractor, TransactionRepository transactionRepository) {
        this.blockchainExtractor = blockchainExtractor;
        this.transactionRepository = transactionRepository;
    }

    public void saveDataToDatabase() {
        saveTransactionsToDatabase(blockchainExtractor.extractBlockchainData());
    }

    private void saveTransactionsToDatabase(Map<Blockchain, List<TransactionResponse>> transactionResponseMap) {
        Set<String> nullTransactionsId = transactionRepository.findAllWithNullChange()
                .stream()
                .map(TransactionEntity::getId)
                .collect(Collectors.toSet());

        List<String> filteredTransactionsToUpdate = transactionResponseMap.values().stream()
                .flatMap(List::stream) // spłaszczanie listy list transakcji do pojedynczego strumienia
                .map(TransactionResponse::getId) // filtruj transakcje, wykluczając te z nullTransactionsId
                .filter(nullTransactionsId::contains)
                .toList(); // zbieranie wyników do listy


        List<TransactionEntity> transactionsToUpdate = transactionRepository.findAllById(filteredTransactionsToUpdate);
        for (TransactionEntity transaction : transactionsToUpdate) {
            transaction.setTwentyFourHourChange(transaction.getTwentyFourHourChange());
            transaction.setAllTransactions(transaction.getAllTransactions());
            nullTransactionsId.remove(transaction.getId());
        }

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
        List<TransactionEntity> transactionsEntitiesToSave = new ArrayList<>(newTransactions.stream()
                .map(transaction -> TransactionEntity.builder()
                        .id(transaction.getId())
                        .chain(transaction.getChain())
                        .date(transaction.getDate())
                        .twentyFourHourChange(transaction.getChain().equals("Solana") ? twentyFourHourChangeTransaction(transaction) : transaction.getTwentyFourHourChange())
                        .allTransactions(transaction.getAllTransactions())
                        .build()).toList());

        transactionResponseMap.forEach((blockchain, transactionResponses) -> {
            String previousDate = blockchain.getName() + "-" + getPreviousDate();

            // Sprawdź, czy dla danego blockchain nie ma transakcji i czy previousDate nie jest już zapisane
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
        transactionsEntitiesToSave.addAll(transactionsToUpdate);
        if (!transactionsEntitiesToSave.isEmpty()) {
            getBatches(transactionsEntitiesToSave).forEach(transactionRepository::saveAll);
        }
    }

    private Long twentyFourHourChangeTransaction(TransactionResponse transactionResponse) {
        Optional<TransactionEntity> previousTransactionEntity = transactionRepository.findFirstByChainOrderByDateDesc(transactionResponse.getChain());
        if (previousTransactionEntity.isPresent() && previousTransactionEntity.get().getAllTransactions() != null) {
            return transactionResponse.getAllTransactions() - previousTransactionEntity.get().getAllTransactions();
        } else return null;

    }

    private static String getPreviousDate() {
        LocalDate localDate = LocalDate.now(ZONE_ID).minusDays(1);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-dd-yyy");
        return localDateTime.format(formatter);
    }

    private static long getPreviousTimestamp() {
        LocalDate localDate = LocalDate.now(ZONE_ID).minusDays(1);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        return localDateTime.atZone(ZONE_ID).toEpochSecond();
    }

    private static <T> List<List<T>> getBatches(List<T> collection) {
        return IntStream.iterate(0, i -> i < collection.size(), i -> i + DatabaseService.BATCH_SIZE)
                .mapToObj(i -> collection.subList(i, Math.min(i + DatabaseService.BATCH_SIZE, collection.size())))
                .toList();
    }
}
