package tracker.transactionstracker.extractor.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class AvalancheResponse {
    @JsonProperty("results")
    private List<Results> response;
    @Data
    @NoArgsConstructor
    public static class Results{
        private long value;
        private long timestamp;
    }
}
