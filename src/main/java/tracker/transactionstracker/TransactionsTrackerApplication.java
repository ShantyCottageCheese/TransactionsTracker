package tracker.transactionstracker;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Marker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cglib.core.Block;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.error.Mark;
import tracker.transactionstracker.correlations.*;
import tracker.transactionstracker.extractor.Blockchain;
import tracker.transactionstracker.model.TransactionEntity;

import javax.crypto.spec.PSource;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


@EnableScheduling
@SpringBootApplication
@Slf4j
public class TransactionsTrackerApplication {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        int mb = 1024 * 1024;
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long xmx = memoryBean.getHeapMemoryUsage().getMax() / mb;
        long xms = memoryBean.getHeapMemoryUsage().getInit() / mb;

        log.info("Initial Memory (xms) : {} mb", xms);
        log.info("Max Memory (xmx) : {} mb", xmx);

        SpringApplication.run(TransactionsTrackerApplication.class, args);
        //for (Blockchain name : Blockchain.values()) {
        //String ticker = name.getTokenName() + "usd";
           /* String ticker = "near" + "usd";
            String freq = "1day";
            String apiKey = "e5f8dee2c78aa68188a69bfa8eb1b6ec7a9e8262";
            String startDate = "2022-01-20";
            String endDate = "2022-01-22";
            try {
                String dataTemp = restTemplate.getForObject("https://api.tiingo.com/tiingo/crypto/prices?tickers=" + ticker + "&startDate=" + startDate + "&endDate=" + endDate + "&resampleFreq=" + freq + "&token=" + apiKey, String.class);
                assert dataTemp != null;
                List<CoinResponse> tickerDataList = JSONMapper.ofList(dataTemp, new TypeReference<>() {
                });

                System.out.println(tickerDataList);
            } catch (Exception e) {
                log.warn("Error: " , e);
            }
            Thread.sleep(3000);*/

        //   }

        //map.forEach((key, value) -> System.out.println(key + " " + value));
    }
}
