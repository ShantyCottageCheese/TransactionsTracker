package tracker.transactionstracker.extractor.response;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Data
@Builder
public class TransactionResponse {
    private String id;
    private String chain;
    private Long date;
    private Long twentyFourHourChange;
    private Long allTransactions;
    public LocalDate getLocalDate() {
        return Instant.ofEpochMilli(date).atZone(ZoneId.of(("UTC"))).toLocalDate();
    }
}
