package tracker.transactionstracker.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@Data
public class SuiResponse {
    @JsonProperty("value")
    private Double value;
    @JsonProperty("chart")
    private List<Hourly> result;

    @NoArgsConstructor
    @Data
    public static class Hourly {
        private Double value;
        private Long timestamp;
    }
}
