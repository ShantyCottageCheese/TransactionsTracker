package tracker.transactionstracker.extractor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tracker.transactionstracker.response.TransactionResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class BlockchainController {

    private final BlockchainExtractor blockchainExtractor;

    public BlockchainController(BlockchainExtractor blockchainExtractor) {
        this.blockchainExtractor = blockchainExtractor;
    }

    @GetMapping("/extract-blockchain-data")
    public ResponseEntity<Map<Blockchain, Optional<List<TransactionResponse>>>> extractBlockchainData() {
        Map<Blockchain, Optional<List<TransactionResponse>>> data = blockchainExtractor.extractBlockchainData();
        return ResponseEntity.ok(data);
    }
}
