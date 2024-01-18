package extractor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import processors.BlockchainDataHandler;
import response.TransactionResponse;

import java.util.*;
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

    public Map<Blockchain, Optional<List<TransactionResponse>>> extractBlockchainData() {
        return Arrays.stream(Blockchain.values())
                .collect(Collectors.toMap(
                        Function.identity(),
                        this::fetchDataForBlockchain));
    }

    private Optional<List<TransactionResponse>> fetchDataForBlockchain(Blockchain type) {
        return Optional.ofNullable(handlers.get(type))
                .map(handler -> handler.extractData(type.getUrl(), type.getName()));
    }

    private Optional<String> convertHandlerNameToEnum(String handlerName) {
        if (handlerName != null && handlerName.endsWith("DataHandler")) {
            String enumName = handlerName.substring(0, handlerName.length() - "DataHandler".length());
            return Optional.of(enumName.toUpperCase());
        } else {
            return Optional.empty();
        }
    }


}
