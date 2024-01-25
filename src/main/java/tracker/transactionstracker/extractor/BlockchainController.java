package tracker.transactionstracker.extractor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tracker.transactionstracker.correlations.CorrelationService;
import tracker.transactionstracker.databse.DatabaseService;
import tracker.transactionstracker.extractor.response.TransactionResponse;

import java.util.List;
import java.util.Map;

@RestController
public class BlockchainController {

    private final BlockchainExtractor blockchainExtractor;
    private final DatabaseService databaseService;
    private final CorrelationService correlationService;

    public BlockchainController(BlockchainExtractor blockchainExtractor, DatabaseService databaseService, CorrelationService correlationService) {
        this.blockchainExtractor = blockchainExtractor;
        this.databaseService = databaseService;
        this.correlationService = correlationService;
    }

    @GetMapping("/extract-blockchain-data")
    public ResponseEntity<Map<Blockchain, List<TransactionResponse>>> extractBlockchainData() {
        long start = System.currentTimeMillis();

        Map<Blockchain, List<TransactionResponse>> data = blockchainExtractor.extractBlockchainData();

        long end = System.currentTimeMillis();
        long elapsedTime = end - start;

        System.out.println("Czas wykonania: " + elapsedTime + " milisekund");
        return ResponseEntity.ok(data);
    }

    @GetMapping("save-data")
    public ResponseEntity<String> saveData() {
        long start = System.currentTimeMillis();

        databaseService.saveDataToDatabase();

        long end = System.currentTimeMillis();
        long elapsedTime = end - start;
        System.out.println("Czas wykonania: " + elapsedTime + " milisekund");

        return ResponseEntity.ok("Data saved to database");
    }

    @GetMapping("correlation")
    public ResponseEntity<String> correlation() {
        long start = System.currentTimeMillis();


        long end = System.currentTimeMillis();
        long elapsedTime = end - start;
        System.out.println("Czas wykonania: " + elapsedTime + " milisekund");

        return ResponseEntity.ok(correlationService.test().toString());
    }

}
