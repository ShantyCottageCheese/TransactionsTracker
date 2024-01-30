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

    public Map<String, CryptoPriceHistory> getMarketData(int days) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(MarketData.class, new MarketDataDeserializer());
        mapper.registerModule(module);

        Map<String, CryptoPriceHistory> pricesMap;
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

        List<Future<Map.Entry<String, CryptoPriceHistory>>> futures = new ArrayList<>();

            for (Blockchain name : Blockchain.values()) {
                Future<Map.Entry<String, CryptoPriceHistory>> future = executor.submit(() -> {
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
                    CryptoPriceHistory cryptoPriceHistory = new CryptoPriceHistory();
                    for (MarketData marketData : marketDataList) {
                        cryptoPriceHistory.getPriceHistory().putIfAbsent(marketData.getDate(), marketData.calculateAveragePrice());
                    }
                    return new AbstractMap.SimpleImmutableEntry<>(name.getName(),cryptoPriceHistory);
                });

                futures.add(future);
            }

            pricesMap = futures.stream()
                    .map(f -> {
                        try {
                            return f.get(); // Get the result of each future
                        } catch (InterruptedException | ExecutionException e) {
                            log.warn("Error: ", e);
                            Thread.currentThread().interrupt();
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            executor.shutdown();
        }

        return pricesMap;
    }
}