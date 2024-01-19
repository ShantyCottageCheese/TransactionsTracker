package tracker.transactionstracker.extractor.handlers;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tracker.transactionstracker.extractor.handlers.utils.Utils;
import tracker.transactionstracker.extractor.response.AptosResponse;
import tracker.transactionstracker.extractor.response.TransactionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Lazy
public class AptosDataHandler implements BlockchainDataHandler {

    private final RestTemplate restTemplate;

    public AptosDataHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Override
    public Optional<AptosResponse> getDataFromBlockchain(String url) {
        AptosResponse aptosResponse = new AptosResponse();
        try {
            String response = restTemplate.getForObject(url, String.class);
            if (response != null && !response.isEmpty()) {
                JSONObject obj = new JSONObject(response);
                JSONArray myArray = obj.optJSONArray("daily_user_transactions");
                List<AptosResponse.AptosData> aptosData = aptosResponse.getData();
                for (int i = 0; i < myArray.length(); i++) {
                    aptosData.add(AptosResponse.AptosData.builder()
                            .dayCount(myArray.getJSONObject(i).getLong("num_user_transactions"))
                            .timeStamp(myArray.getJSONObject(i).getString("date")).build());
                }
                return Optional.of(aptosResponse);
            } else {
                log.error("Empty response for Aptos");
                return Optional.empty();
            }
        } catch (RestClientException | JSONException e) {
            log.error("Aptos - resource access error: {}", e.getMessage());
            return Optional.empty();
        }
    }


    @Override
    public List<TransactionResponse> extractData(String url, String chain) {
        List<TransactionResponse> transactionsList = new ArrayList<>();

        Optional<AptosResponse> aptosResponse = getDataFromBlockchain(url);
        aptosResponse.ifPresentOrElse(response -> response.getData().forEach(transaction -> {
            TransactionResponse transactionResponse = TransactionResponse.builder()
                    .id(Utils.getChain(chain) + Utils.convertUnixSecondToDate(Utils.convertDateToUnix(transaction.getTimeStamp())))
                    .date(Utils.convertDateToUnix(transaction.getTimeStamp()))
                    .chain(chain)
                    .twentyFourHourChange(transaction.getDayCount())
                    .build();
            transactionsList.add(transactionResponse);
        }), () -> log.info(Utils.NO_DATA_FOUND + chain));
        return transactionsList;
    }
}
