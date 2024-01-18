package response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AptosResponse {
    @SerializedName("num_user_transactions")
    private long dayCount;

    @SerializedName("date")
    private String timeStamp;
}
