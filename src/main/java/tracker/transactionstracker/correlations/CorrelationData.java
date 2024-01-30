package tracker.transactionstracker.correlations;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class CorrelationData {
    private Map<String, BigDecimal> dateRangeWithCorrelation = new HashMap<>();
}
