package tracker.transactionstracker.extractor.handlers;

import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tracker.transactionstracker.extractor.handlers.utils.Utils;
import tracker.transactionstracker.extractor.response.TransactionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static tracker.transactionstracker.extractor.handlers.utils.Utils.createCommonTransactionResponse;

@Slf4j
@Service
@Lazy
public class SolanaDataHandler implements BlockchainDataHandler {

    private final RestTemplate restTemplate;
    @Value("${TOKEN}")
    private String token;

    public SolanaDataHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<Long> getDataFromBlockchain(String url) {
        Optional<Long> transactionCount = Optional.empty();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("token", token);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String result = response.getBody();
            assert result != null;
            JSONObject jsonObject = new JSONObject(result);
            transactionCount = Optional.of(jsonObject.getLong("transactionCount"));
        } catch (Exception e) {
            log.error("Error while fetching data from blockchain", e);
        }

        return transactionCount;
    }

    @Override
    public List<TransactionResponse> extractData(String url, String chain) {
        List<TransactionResponse> transactionsList = new ArrayList<>();
        Optional<Long> solanaResponse = getDataFromBlockchain(url);
        solanaResponse.ifPresentOrElse(response -> {
            TransactionResponse responseTransaction = TransactionResponse.builder()
                    .id(Utils.getChain(chain) + Utils.getPreviousDate())
                    .date(Utils.convertDateToUnixFromMDY(Utils.getPreviousDate()))
                    .chain(chain)
                    .allTransactions(response)
                    .build();
            transactionsList.add(responseTransaction);
        }, () -> log.info(Utils.NO_DATA_FOUND + chain));
        return transactionsList;
    }


}
