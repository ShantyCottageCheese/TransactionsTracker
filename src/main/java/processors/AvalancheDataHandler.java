package processors;

import com.opencsv.CSVReader;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import response.TransactionResponse;

import java.util.List;
import java.util.Optional;

@Service
@Lazy
public class AvalancheDataHandler implements BlockchainDataHandler {
    private final CommonBlockchainHandler commonBlockchainHandler;

    public AvalancheDataHandler(CommonBlockchainHandler commonBlockchainHandler) {
        this.commonBlockchainHandler = commonBlockchainHandler;
    }

    @Override
    public Optional<CSVReader> getDataFromBlockchain(String url) {
        return commonBlockchainHandler.getDataFromBlockchain(url);
    }

    @Override
    public List<TransactionResponse> extractData(String url, String chain) {
        return commonBlockchainHandler.extractData(url, chain);
    }
}
