package tracker.transactionstracker.correlations;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class BlockchainDataTransaction {
    Map<String, TransactionDto> blockchainData = new HashMap<>();
}
