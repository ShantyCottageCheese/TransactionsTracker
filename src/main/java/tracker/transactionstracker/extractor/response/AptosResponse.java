package tracker.transactionstracker.extractor.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class AptosResponse {
    List<AptosData> data = new ArrayList<>();
    @Data
    @Builder
    public static class AptosData{
        @JsonProperty  ("num_user_transactions")
        private long dayCount;

        @JsonProperty("date")
        private String timeStamp;
    }
}
