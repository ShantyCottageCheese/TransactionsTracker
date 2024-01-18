package tracker.transactionstracker.processors;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tracker.transactionstracker.response.TransactionResponse;
import tracker.transactionstracker.processors.utils.Utils;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Lazy
@Slf4j
public class HarmonyDataHandler implements BlockchainDataHandler {

    @Override
    public Optional<CSVReader> getDataFromBlockchain(String url) {
        if (Utils.downloadCSVFromWebsite(url)) {
            String filePath = Utils.FILE_PATH + Utils.fileName;
            try {
                String csvString = Files.readString(Paths.get(filePath));
                return Optional.ofNullable(new CSVReaderBuilder(new StringReader(csvString))
                        .withSkipLines(1)
                        .build());
            } catch (IOException e) {
                log.error("Harmony - file could not be read: {}", e.getMessage());
            }
        }
        return Optional.empty();
    }

    @Override
    public List<TransactionResponse> extractData(String walletsUrl, String chain) {
        List<TransactionResponse> transactionsList = new ArrayList<>();
        Optional<CSVReader> transactionsResponse = getDataFromBlockchain(walletsUrl);
        transactionsResponse.ifPresentOrElse(reader -> reader.forEach(line -> {
                    TransactionResponse response = TransactionResponse.builder()
                            .id(Utils.getChain(chain) + Utils.convertUnixSecondToDate(Utils.convertDateToUnix(line[0])))
                            .date(Utils.convertDateToUnixFromYMD(line[0]))
                            .chain(chain)
                            .twentyFourHourChange(Long.parseLong(line[1].replace(" ", "")))
                            .build();
                    transactionsList.add(response);
                }),
                () -> log.info("No CSV file found: " + chain));
        return transactionsList;
    }
}
