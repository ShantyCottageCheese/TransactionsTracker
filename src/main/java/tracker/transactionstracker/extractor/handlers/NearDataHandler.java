package tracker.transactionstracker.extractor.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tracker.transactionstracker.extractor.response.NearResponse;
import tracker.transactionstracker.extractor.response.TransactionResponse;
import tracker.transactionstracker.extractor.handlers.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Lazy
@Slf4j
public class NearDataHandler implements BlockchainDataHandler {
    private final RestTemplate restTemplate;

    public NearDataHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<NearResponse> getDataFromBlockchain(String url) {
        try {
            NearResponse response = restTemplate.getForObject(url, NearResponse.class);
            return Optional.ofNullable(response);
        } catch (RestClientException e) {
            log.error("Near - resource access error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<TransactionResponse> extractData(String url, String chain) {
        List<TransactionResponse> transactionsList = new ArrayList<>();

        Optional<NearResponse> nearResponse = getDataFromBlockchain(url);
        nearResponse.ifPresentOrElse(response -> response.getData().forEach(data -> {
            TransactionResponse transactionResponse = TransactionResponse.builder()
                    .id(Utils.getChain(chain) + Utils.convertUnixSecondToDate(Utils.convertDateToUnixFromYMD(data.getDate().substring(0, 10))))
                    .date(Utils.convertDateToUnixFromYMD(data.getDate().substring(0, 10)))
                    .chain(chain)
                    .twentyFourHourChange(data.getTxns())
                    .build();
            transactionsList.add(transactionResponse);
        }), () -> log.info(Utils.NO_DATA_FOUND + chain));
        return transactionsList;

    }
}
