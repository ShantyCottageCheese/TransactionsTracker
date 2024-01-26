package tracker.transactionstracker.marketdata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.math.BigDecimal;

import static tracker.transactionstracker.marketdata.Utils.convertDateFromMilliSecond;

public class MarketDataDeserializer extends JsonDeserializer<MarketData> {

    @Override
    public MarketData deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        return new MarketData(
                 convertDateFromMilliSecond( node.get(0).asLong()),
                new BigDecimal(node.get(1).asText()),
                new BigDecimal(node.get(2).asText()),
                new BigDecimal(node.get(3).asText()),
                new BigDecimal(node.get(4).asText()),
                new BigDecimal(node.get(5).asText()),
                node.get(6).asLong(),
                new BigDecimal(node.get(7).asText()),
                node.get(8).asInt(),
                new BigDecimal(node.get(9).asText()),
                new BigDecimal(node.get(10).asText()),
                node.get(11).asText()
        );
    }
}