package processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import response.SuiResponse;
import response.TransactionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static processors.utils.Utils.*;

@Slf4j
@Service
@Lazy
public class SuiDataHandler implements BlockchainDataHandler {
    private final RestTemplate restTemplate;

    public SuiDataHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<SuiResponse> getDataFromBlockchain(String url) {
        try {
            SuiResponse response = restTemplate.getForObject(url, SuiResponse.class);
            return Optional.ofNullable(response);
        } catch (RestClientException e) {
            log.error("Sui", e);
            return Optional.empty();
        }
    }

    @Override
    public List<TransactionResponse> extractData(String url, String chain) {
        List<TransactionResponse> transactionsList = new ArrayList<>();
        Optional<SuiResponse> suiResponse = getDataFromBlockchain(url);
        suiResponse.ifPresentOrElse(response -> {
            List<SuiResponse.Result> results = response.getResult();
            for (int i = 0; i < results.size() - 1; i++) {
                SuiResponse.Result data = results.get(i);
                TransactionResponse transactionResponse = TransactionResponse.builder()
                        .id(getChain(chain) + convertUnixMilisecondToNormalDate(data.getDateTime()))
                        .date(data.getDateTime() / 1000)
                        .chain(chain)
                        .twentyFourHourChange((long) data.getCount())
                        .build();
                transactionsList.add(transactionResponse);
            }
        }, () -> log.info(noDataFound + chain));
        return transactionsList;
    }
}
