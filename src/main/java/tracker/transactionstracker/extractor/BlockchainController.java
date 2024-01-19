package tracker.transactionstracker.extractor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tracker.transactionstracker.extractor.response.TransactionResponse;

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
        long start = System.currentTimeMillis();
        Map<Blockchain, Optional<List<TransactionResponse>>> data = blockchainExtractor.extractBlockchainData();
        long end = System.currentTimeMillis();
        long elapsedTime = end - start;
        System.out.println("Czas wykonania: " + elapsedTime + " milisekund");
        return ResponseEntity.ok(data);
    }
}
