package processors;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import response.TransactionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static processors.utils.Utils.*;

@Slf4j
@Service
@Lazy
public class CosmosDataHandler implements BlockchainDataHandler {
    private final RestTemplate restTemplate;

    public CosmosDataHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<Long> getDataFromBlockchain(String url) {
        String response;
        try {
            response = restTemplate.getForObject(url, String.class);
            if (response != null && !response.isEmpty()) {
                JSONObject obj = new JSONObject(response);
                Long count = obj.getJSONObject("data").getLong("num_txs_yesterday");
                return Optional.of(count);
            } else {
                log.error("Empty response for Cosmos");
                return Optional.empty();
            }
        } catch (RestClientException | JSONException e) {
            log.error("Cosmos - resource access error: {}", e.getMessage());
            return Optional.empty();
        }
    }
    @Override
    public List<TransactionResponse> extractData(String url, String chain) {
        Optional<Long> cosmosResponse = getDataFromBlockchain(url);
        List<TransactionResponse> transactionsList = new ArrayList<>();

        cosmosResponse.ifPresentOrElse(response -> {
            TransactionResponse responseTransaction = TransactionResponse.builder()
                    .id(getChain(chain)+getPreviousDate())
                    .date(convertDateToUnixSpecialFormatTwo(getPreviousDateToString()))
                    .chain(chain)
                    .twentyFourHourChange(response)
                    .build();
            transactionsList.add(responseTransaction);
        }, () -> log.info(noDataFound + chain));
        return transactionsList;
    }
}
