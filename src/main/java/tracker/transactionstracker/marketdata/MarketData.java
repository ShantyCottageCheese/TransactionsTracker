package tracker.transactionstracker.marketdata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MarketData {
    private String date;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private BigDecimal volume;
    private Long closeTime;
    private BigDecimal quoteAssetVolume;
    private Integer numberOfTrades;
    private BigDecimal takerBuyBaseAssetVolume;
    private BigDecimal takerBuyQuoteAssetVolume;
    private String ignore;
    public BigDecimal calculateAveragePrice() {
        BigDecimal sum = openPrice.add(highPrice).add(lowPrice).add(closePrice);
        return sum.divide(BigDecimal.valueOf(4), RoundingMode.HALF_UP).setScale(3,RoundingMode.HALF_UP);
    }
}
