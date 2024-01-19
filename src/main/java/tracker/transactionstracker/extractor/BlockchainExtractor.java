package tracker.transactionstracker.extractor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tracker.transactionstracker.extractor.handlers.BlockchainDataHandler;
import tracker.transactionstracker.extractor.response.TransactionResponse;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlockchainExtractor {
    private final Map<Blockchain, BlockchainDataHandler> handlers;

    public BlockchainExtractor(List<BlockchainDataHandler> handlerList) {
        handlers = new EnumMap<>(Blockchain.class);
        for (BlockchainDataHandler handler : handlerList) {
            convertHandlerNameToEnum(handler.getClass().getSimpleName())
                    .flatMap(name -> {
                        try {
                            return Optional.of(Blockchain.valueOf(name));
                        } catch (IllegalArgumentException e) {
                            log.warn("Enum for handler not found: {}", name);
                            return Optional.empty();
                        }
                    })
                    .ifPresent(blockchain -> handlers.put(blockchain, handler));
        }
    }

    /*public Map<Blockchain, Optional<List<TransactionResponse>>> extractBlockchainData() {
         return Arrays.stream(Blockchain.values())
                 .collect(Collectors.toMap(
                         Function.identity(),
                         this::fetchDataForBlockchain));
     }*/
    public Map<Blockchain, List<TransactionResponse>> extractBlockchainData() {
        Map<Blockchain, Future<List<TransactionResponse>>> futures;

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            futures = Arrays.stream(Blockchain.values())
                    .collect(Collectors.toMap(
                            Function.identity(),
                            blockchain -> CompletableFuture.supplyAsync(() -> fetchDataForBlockchain(blockchain), executor)
                    ));
        }

        return futures.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            try {
                                return entry.getValue().get();
                            } catch (InterruptedException | ExecutionException e) {
                                log.error("Error fetching data for blockchain: {}", entry.getKey(), e);
                                return Collections.emptyList();
                            }
                        }
                ));
    }

    private List<TransactionResponse> fetchDataForBlockchain(Blockchain type) {
        return handlers.get(type).extractData(type.getUrl(),type.getName());
    }

    private Optional<String> convertHandlerNameToEnum(String handlerName) {
        if (handlerName != null && handlerName.endsWith("DataHandler")) {
            return Optional.of(handlerName.substring(0, handlerName.length() - "DataHandler".length()).toUpperCase());
        } else {
            return Optional.empty();
        }
    }


}
