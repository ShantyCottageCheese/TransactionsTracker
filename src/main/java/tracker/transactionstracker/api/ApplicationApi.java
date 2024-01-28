package tracker.transactionstracker.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tracker.transactionstracker.charts.ChartService;
import tracker.transactionstracker.databse.DatabaseService;
import tracker.transactionstracker.extractor.Blockchain;
import tracker.transactionstracker.extractor.BlockchainExtractor;
import tracker.transactionstracker.extractor.response.TransactionResponse;

import java.util.List;
import java.util.Map;

@RestController
public class ApplicationApi {
    private final ChartService chartService;
    private final DatabaseService databaseService;
    private final BlockchainExtractor blockchainExtractor;


    public ApplicationApi(ChartService chartService, DatabaseService databaseService, BlockchainExtractor blockchainExtractor) {
        this.chartService = chartService;
        this.databaseService = databaseService;
        this.blockchainExtractor = blockchainExtractor;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.status(200).body("OK");
    }

    @GetMapping("/heatmap")
    public ResponseEntity<String> generateChart(@RequestParam int days) {
        String heatmap = chartService.generateHeatmap(days);
        return ResponseEntity.ok(heatmap);
    }

    @PostMapping("saveData")
    public ResponseEntity<String> saveData() {
        Map<Blockchain, List<TransactionResponse>> transactions = blockchainExtractor.extractBlockchainData();
        databaseService.saveTransactionsToDatabase(transactions);
        return ResponseEntity.ok("Data saved to database.");
    }
}
