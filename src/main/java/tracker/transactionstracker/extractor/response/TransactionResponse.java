package tracker.transactionstracker.extractor.response;

import lombok.*;

@Data
@Builder
public class TransactionResponse {
    private String id;
    private String chain;
    private Long date;
    private Long twentyFourHourChange;
    private Long allTransactions;
}
