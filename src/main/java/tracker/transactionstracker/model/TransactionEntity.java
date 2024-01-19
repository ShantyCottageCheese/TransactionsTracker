package tracker.transactionstracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name = "TRANSACTIONS_TRACKER")
public class TransactionEntity {
    @Id
    private String id;
    @Column
    private String chain;
    @Column
    private Long date;
    @Column(name = "transaction_24h")
    private Long twentyFourHourChange;
    @Column(name= "transactions_all")
    private Long allTransactions;
}
