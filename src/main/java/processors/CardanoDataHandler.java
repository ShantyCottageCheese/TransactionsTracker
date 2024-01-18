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
public class CardanoDataHandler implements BlockchainDataHandler {
    private final RestTemplate restTemplate;

    public CardanoDataHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<Long> getDataFromBlockchain(String url) {
        String response = "";
        try {
            response = restTemplate.getForObject(url, String.class);
            if (response != null && !response.isEmpty()) {
                JSONObject obj = new JSONObject(response);
                Long count = obj.getJSONObject("data").getLong("tx");
                return Optional.of(count);
            } else {
                log.info("Empty response for Cardano");
                return Optional.empty();
            }
        } catch (RestClientException | JSONException ex) {
            log.warn("Exception {} during communication with API on URL {} with request body {}", ex.getMessage(), url, response);
            return Optional.empty();
        }
    }

    @Override
    public List<TransactionResponse> extractData(String url, String chain) {
        List<TransactionResponse> transactionsList = new ArrayList<>();
        Optional<Long> cardanoResponse = getDataFromBlockchain(url);
        cardanoResponse.ifPresentOrElse(response -> {
            TransactionResponse transactionResponse = TransactionResponse.builder()
                    .id(getChain(chain) + getPreviousDateToString())
                    .date(convertDateToUnixSpecialFormatTwo(getPreviousDateToString()))
                    .chain(chain)
                    .allTransactions(response)
                    .build();
            transactionsList.add(transactionResponse);
        }, () -> log.info(noDataFound + chain));
        return transactionsList;
    }
}
