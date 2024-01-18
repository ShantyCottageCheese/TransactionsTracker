package processors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import response.AptosResponse;
import response.TransactionResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static processors.utils.Utils.*;

@Slf4j
@Service
@Lazy
public class AptosDataHandler implements BlockchainDataHandler {

    private final RestTemplate restTemplate;

    public AptosDataHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<AptosResponse[]> getDataFromBlockchain(String url) {
        try {
            String response = restTemplate.getForObject(url, String.class);
            if (response != null) {
                Gson gson = new Gson();
                JsonObject rootObject = gson.fromJson(response, JsonObject.class);
                JsonElement myArrayElement = rootObject.get("daily_user_transactions");
                if (myArrayElement != null && myArrayElement.isJsonArray()) {
                    AptosResponse[] myArray = gson.fromJson(myArrayElement, AptosResponse[].class);
                    return Optional.ofNullable(myArray);
                }
            }
        } catch (Exception e) {
            log.error("Aptos - resource access error: {}", e.getMessage());
        }
        return Optional.empty();

    }

    @Override
    public List<TransactionResponse> extractData(String url, String chain) {
        List<TransactionResponse> transactionsList = new ArrayList<>();

        Optional<AptosResponse[]> aptosResponse = getDataFromBlockchain(url);
        aptosResponse.ifPresentOrElse(response -> Arrays.stream(response).skip(1).iterator().forEachRemaining(transaction -> {
            TransactionResponse transactionResponse = TransactionResponse.builder()
                    .id(getChain(chain) + convertUnixSecondToNormalDate(convertDateToUnix(transaction.getTimeStamp())))
                    .date(convertDateToUnix(transaction.getTimeStamp()))
                    .chain(chain)
                    .twentyFourHourChange(transaction.getDayCount())
                    .build();
            transactionsList.add(transactionResponse);
        }), () -> log.info(noDataFound + chain));
        return transactionsList;
    }
}
