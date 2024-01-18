package processors;

import response.TransactionResponse;

import java.util.List;
import java.util.Optional;

public interface BlockchainDataHandler {
    Optional<?> getDataFromBlockchain(String url);

    List<TransactionResponse> extractData(String url, String chain);
}
