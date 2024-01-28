package tracker.transactionstracker.marketdata;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tracker.transactionstracker.extractor.Blockchain;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BinanceService {

    private final SpotClient client = new SpotClientImpl();

    public Map<String, Map<String, BigDecimal>> getMarketData(int days) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(MarketData.class, new MarketDataDeserializer());
        mapper.registerModule(module);

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        List<Future<Map.Entry<String, Map<String, BigDecimal>>>> futures = new ArrayList<>();

        for (Blockchain name : Blockchain.values()) {
            Future<Map.Entry<String, Map<String, BigDecimal>>> future = executor.submit(() -> {
                Map<String, Object> parameters = new LinkedHashMap<>();
                parameters.put("symbol", name.getTicker());
                parameters.put("interval", "1d");
                parameters.put("limit", days);
                String result = client.createMarket().klines(parameters);

                List<MarketData> marketDataList;
                try {
                    marketDataList = mapper.readValue(result,
                            mapper.getTypeFactory().constructCollectionType(List.class, MarketData.class));
                } catch (JsonProcessingException e) {
                    log.warn("Error: ", e);
                    return null;
                }
                if (marketDataList.isEmpty()) {
                    return null;
                }
                marketDataList.removeLast();

                Map<String, BigDecimal> insideMap = new HashMap<>();
                for (MarketData marketData : marketDataList) {
                    insideMap.put(marketData.getDate(), marketData.calculateAveragePrice());
                }
                return new AbstractMap.SimpleImmutableEntry<>(name.getName(), insideMap);
            });

            futures.add(future);
        }

        Map<String, Map<String, BigDecimal>> pricesMap = futures.stream()
                .map(f -> {
                    try {
                        return f.get(); // Get the result of each future
                    } catch (InterruptedException | ExecutionException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        executor.shutdown();

        return pricesMap;
    }
}