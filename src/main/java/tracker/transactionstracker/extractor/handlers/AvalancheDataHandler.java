package tracker.transactionstracker.extractor.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tracker.transactionstracker.extractor.handlers.utils.Utils;
import tracker.transactionstracker.extractor.response.AvalancheResponse;
import tracker.transactionstracker.extractor.response.TransactionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Lazy
@Slf4j
public class AvalancheDataHandler implements BlockchainDataHandler {
    private final RestTemplate restTemplate;


    public AvalancheDataHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<AvalancheResponse> getDataFromBlockchain(String url) {
        try {
            AvalancheResponse response = restTemplate.getForObject(url, AvalancheResponse.class);
            return Optional.ofNullable(response);
        } catch (RestClientException e) {
            log.error("Near - resource access error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<TransactionResponse> extractData(String url, String chain) {
        List<TransactionResponse> transactionsList = new ArrayList<>();
        Optional<AvalancheResponse> avalancheResponse = getDataFromBlockchain(url);
        avalancheResponse.ifPresentOrElse(response -> response.getResponse().stream().skip(1).forEach(data -> {
            TransactionResponse transactionResponse = TransactionResponse.builder()
                    .id(Utils.getChain(chain) + Utils.convertUnixSecondToDate(data.getTimestamp()))
                    .date(data.getTimestamp())
                    .chain(chain)
                    .twentyFourHourChange(data.getValue())
                    .build();
            transactionsList.add(transactionResponse);
        }), () -> log.info(Utils.NO_DATA_FOUND + chain));
        return transactionsList;
    }
}
