package tracker.transactionstracker.marketdata;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
@Data
@NoArgsConstructor
public class CryptoPriceHistory {

    private Map<String, BigDecimal> priceHistory = new HashMap<>();
}
