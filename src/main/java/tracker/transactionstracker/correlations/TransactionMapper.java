package tracker.transactionstracker.correlations;

import tracker.transactionstracker.model.TransactionEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static tracker.transactionstracker.marketdata.Utils.convertDateFromSecond;

public class TransactionMapper {
    public static Map<String, Map<String, TransactionDto>> convertEntityToDto(Map<String, List<TransactionEntity>> transactions, Map<String, Map<String, BigDecimal>> pricesMap ){
        Map<String, Map<String, TransactionDto>> result = new HashMap<>();

        for (Map.Entry<String, List<TransactionEntity>> entry : transactions.entrySet()) {
            String blockchainName = entry.getKey();
            Map<String, BigDecimal> priceMap = pricesMap.get(blockchainName);
            if (priceMap == null)
                continue;


            List<TransactionEntity> transactionList = entry.getValue();
            result.putIfAbsent(blockchainName, new HashMap<>());
            result.put(blockchainName, transactionList.stream().map(transaction -> {
                String date = convertDateFromSecond(transaction.getDate());
                return new TransactionDto.TransactionDtoBuilder()
                        .chain(blockchainName)
                        .date(date)
                        .twentyFourHourChange(transaction.getTwentyFourHourChange())
                        .allTransactions(transaction.getAllTransactions())
                        .price(priceMap.get(date))
                        .build();
            }).collect(Collectors.toMap(TransactionDto::getDate, Function.identity())));
        }
        return result;
    }
}
