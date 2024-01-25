package tracker.transactionstracker.marketdata;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tracker.transactionstracker.extractor.Blockchain;

import java.math.BigDecimal;
import java.util.*;

@Service
@Lazy
@Slf4j
public class BinanceService {

    private final SpotClient client = new SpotClientImpl();

    public Map<String, Map<String, BigDecimal>> getMarketData(int days) {

        Map<String, Map<String, BigDecimal>> pricesMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        for (Blockchain name : Blockchain.values()) {
            Map<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("symbol", name.getTokenName());
            parameters.put("interval", "1d");
            parameters.put("limit", days);
            String result = client.createMarket().klines(parameters);


            module.addDeserializer(MarketData.class, new MarketDataDeserializer());
            mapper.registerModule(module);

            List<MarketData> marketDataList;
            try {
                marketDataList = mapper.readValue(result,
                        mapper.getTypeFactory().constructCollectionType(List.class, MarketData.class));
            } catch (JsonProcessingException e) {
                log.warn("Error: ", e);
                continue;
            }
            if (marketDataList.isEmpty()) {
                continue;
            }
            marketDataList.removeLast();

            pricesMap.putIfAbsent(name.getName(), new HashMap<>());
            Map<String, BigDecimal> insideMap = pricesMap.get(name.getName());
            for (MarketData marketData : marketDataList) {
                insideMap.put(marketData.getDate(), marketData.calculateAveragePrice());
            }
        }
        return pricesMap;

    }
}
