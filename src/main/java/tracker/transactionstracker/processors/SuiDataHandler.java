package tracker.transactionstracker.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tracker.transactionstracker.response.SuiResponse;
import tracker.transactionstracker.response.TransactionResponse;
import tracker.transactionstracker.processors.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static tracker.transactionstracker.processors.utils.Utils.*;

@Slf4j
@Service
@Lazy
public class SuiDataHandler implements BlockchainDataHandler {
    private final RestTemplate restTemplate;

    public SuiDataHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<Long> getDataFromBlockchain(String url) {
        try {
            SuiResponse response = restTemplate.getForObject(url, SuiResponse.class);
            if (response != null && response.getValue() != null) {
                return Optional.of((response.getValue().longValue()));
            } else {
                return Optional.empty();

            }
        } catch (RestClientException e) {
            log.error("Sui", e);
            return Optional.empty();
        }
    }

    @Override
    public List<TransactionResponse> extractData(String url, String chain) {
        List<TransactionResponse> transactionsList = new ArrayList<>();
        Optional<Long> suiResponse = getDataFromBlockchain(url);
        suiResponse.ifPresentOrElse(response -> {
                TransactionResponse transactionResponse = TransactionResponse.builder()
                        .id(Utils.getChain(chain) + getPreviousDate())
                        .date(convertDateToUnixFromMDY(getPreviousDate()))
                        .chain(chain)
                        .twentyFourHourChange(response)
                        .build();
                transactionsList.add(transactionResponse);
        }, () -> log.info(Utils.noDataFound + chain));
        return transactionsList;
    }
}
