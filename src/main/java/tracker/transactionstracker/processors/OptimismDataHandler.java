package tracker.transactionstracker.processors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tracker.transactionstracker.response.TransactionResponse;

import java.util.List;
import java.util.Optional;
@Service
@Lazy
public class OptimismDataHandler implements BlockchainDataHandler {
    private final CommonBlockchainHandler commonBlockchainHandler;

    public OptimismDataHandler(CommonBlockchainHandler commonBlockchainHandler) {
        this.commonBlockchainHandler = commonBlockchainHandler;
    }

    @Override
    public Optional<?> getDataFromBlockchain(String url) {
        return commonBlockchainHandler.getDataFromBlockchain(url);
    }

    @Override
    public List<TransactionResponse> extractData(String url, String chain) {
        return commonBlockchainHandler.extractData(url, chain);
    }
}
