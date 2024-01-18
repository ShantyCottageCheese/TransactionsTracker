package response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NearResponse {
    @JsonProperty("charts")
    private List<Data> data;
    @lombok.Data
    public static class Data {
        private String date;
        @JsonProperty("near_price")
        private float nearPrice;
        @JsonProperty("market_cap")
        private String marketCap;
        @JsonProperty("total_supply")
        private String totalSupply;
        private long blocks;
        @JsonProperty("gas_fee")
        private float gasFee;
        @JsonProperty("gas_used")
        private float gasUsed;
        @JsonProperty("avg_gas_price")
        private float avgGasPrice;
        @JsonProperty("avg_gas_limit")
        private float avgGasLimit;
        private long txns;
        @JsonProperty("txn_volume")
        private String txnVolume;
        @JsonProperty("txn_volume_usd")
        private float txnVolumeUsd;
        @JsonProperty("txn_fee")
        private String txnFee;
        @JsonProperty("txn_fee_usd")
        private float txnFeeUsd;
        @JsonProperty("total_addresses")
        private long totalAddresses;
        private long addresses;
    }
}