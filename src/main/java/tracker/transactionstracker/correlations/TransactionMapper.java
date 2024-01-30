package tracker.transactionstracker.correlations;

import tracker.transactionstracker.marketdata.CryptoPriceHistory;
import tracker.transactionstracker.model.TransactionEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static tracker.transactionstracker.marketdata.Utils.convertDateFromSecond;

public class TransactionMapper {
    public static Map<String, BlockchainDataTransaction> convertEntityToBlockchainDataTransactionsMap(Map<String, List<TransactionEntity>> transactions, Map<String, CryptoPriceHistory> pricesMap) {
        Map<String, BlockchainDataTransaction> result = new HashMap<>();

        transactions.forEach((blockchainName, transactionList) -> {
            CryptoPriceHistory priceMap = pricesMap.get(blockchainName);
            if (priceMap == null) {
                return;
            }
            BlockchainDataTransaction blockchainDataTransaction = new BlockchainDataTransaction();

            Map<String, TransactionDto> transactionDtoMap = transactionList.stream()
                    .collect(Collectors.toMap(
                            transaction -> convertDateFromSecond(transaction.getDate()),
                            transaction -> new TransactionDto.TransactionDtoBuilder()
                                    .chain(blockchainName)
                                    .date(convertDateFromSecond(transaction.getDate()))
                                    .twentyFourHourChange(transaction.getTwentyFourHourChange())
                                    .allTransactions(transaction.getAllTransactions())
                                    .price(priceMap.getPriceHistory().get(convertDateFromSecond(transaction.getDate())))
                                    .build()
                    ));

            blockchainDataTransaction.setBlockchainData(transactionDtoMap);
            result.put(blockchainName, blockchainDataTransaction);
        });

        return result;
    }
}
