package processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import response.NearResponse;
import response.TransactionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static processors.utils.Utils.*;

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
                    .id(getChain(getChain(chain) + convertUnixSecondToNormalDate(convertDateToUnixForNearAndCosmos(data.getDate()))))
                    .date(convertDateToUnixForNearAndCosmos(Objects.requireNonNull(data.getDate())))
                    .chain(chain)
                    .twentyFourHourChange(data.getTxns())
                    .build();
            transactionsList.add(transactionResponse);
        }), () -> log.info(noDataFound + chain));
        return transactionsList;

    }
}
