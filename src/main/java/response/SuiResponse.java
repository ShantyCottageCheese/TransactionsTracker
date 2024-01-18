package response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SuiResponse {
    private int code;
    private String message;
    private List<Result> result;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Result {
        private long dateTime;
        private int count;
        private boolean isAccumulate;
        private String typeName;
    }
}
