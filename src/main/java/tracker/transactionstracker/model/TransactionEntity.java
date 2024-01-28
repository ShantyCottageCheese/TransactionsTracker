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
@Entity
public class TransactionEntity {
    @Id
    private String id;
    @Column
    private String chain;
    @Column
    private Long date;
    @Column
    private Long twentyFourHourChange;
    @Column
    private Long allTransactions;
}
