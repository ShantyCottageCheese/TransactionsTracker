package tracker.transactionstracker.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tracker.transactionstracker.charts.ChartService;
import tracker.transactionstracker.databse.DatabaseService;

@RestController
public class ApplicationApi {
    private final ChartService chartService;
    private final DatabaseService databaseService;

    public ApplicationApi(ChartService chartService, DatabaseService databaseService) {
        this.chartService = chartService;
        this.databaseService = databaseService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/heatmap")
    public ResponseEntity<String> generateChart(@RequestParam int days) {
        String heatmap = chartService.generateHeatmap(days);
        return ResponseEntity.ok(heatmap);
    }

    @PostMapping("save-data")
    public ResponseEntity<String> saveData() {
        databaseService.saveDataToDatabase();

        return ResponseEntity.ok("Data saved to database");
    }
}
