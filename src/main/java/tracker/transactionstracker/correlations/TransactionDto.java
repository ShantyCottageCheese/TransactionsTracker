package tracker.transactionstracker.correlations;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionDto {
    private String chain;
    private String date;
    private Long twentyFourHourChange;
    private Long allTransactions;
    private BigDecimal price;
}
