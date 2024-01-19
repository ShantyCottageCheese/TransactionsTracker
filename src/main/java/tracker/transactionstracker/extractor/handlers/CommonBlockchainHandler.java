package tracker.transactionstracker.extractor.handlers;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tracker.transactionstracker.extractor.response.TransactionResponse;
import tracker.transactionstracker.extractor.handlers.utils.Utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Lazy
public class CommonBlockchainHandler implements BlockchainDataHandler {
    private final RestTemplate restTemplate;

    public CommonBlockchainHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<CSVReader> getDataFromBlockchain(String url) {
        try {
            String csvString = restTemplate.getForObject(url, String.class);
            if (csvString != null) {
                return Optional.of(new CSVReaderBuilder(new StringReader(csvString)).withSkipLines(1).build());
            }
        } catch (Exception e) {
            log.error("An error occurred while creating CSVReader for URL: " + url, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<TransactionResponse> extractData(String url, String chain) {
        List<TransactionResponse> transactionsList = new ArrayList<>();
        Optional<CSVReader> transactionResponse = getDataFromBlockchain(url);
        transactionResponse.ifPresentOrElse(reader -> reader.forEach(line -> {
                    TransactionResponse response = TransactionResponse.builder()
                            .id(Utils.getId(line, chain))
                            .date(Long.parseLong(line[1]))
                            .chain(chain)
                            .twentyFourHourChange(Long.parseLong(line[2]))
                            .build();
                    transactionsList.add(response);

                }),
                () -> log.info("No CSV file found: {} ", chain));
        return transactionsList;
    }
}
