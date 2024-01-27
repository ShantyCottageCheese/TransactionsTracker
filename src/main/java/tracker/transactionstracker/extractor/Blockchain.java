package tracker.transactionstracker.extractor;

import lombok.Getter;

@Getter
public enum Blockchain {
    ARBITRUM("Arbitrum", "ARBUSDT", "https://arbiscan.io/chart/tx?output=csv"),
    AVALANCHE("Avalanche", "AVAXUSDT", "https://metrics.avax.network/v1/tx_count/mainnet?interval=day"),
    BSC("Bsc", "BNBUSDT", "https://bscscan.com/chart/tx?output=csv"),
    COSMOS("Cosmos", "ATOMUSDT", "https://index.atomscan.com/blocks/average"),
    ETHEREUM("Ethereum", "ETHUSDT", "https://etherscan.io/chart/tx?output=csv"),
    FANTOM("Fantom", "FTMUSDT", "https://ftmscan.com/chart/tx?output=csv"),
    OPTIMISM("Optimism", "OPUSDT", "https://optimistic.etherscan.io/chart/tx?output=csv"),
    POLYGON("Polygon", "MATICUSDT", "https://polygonscan.com/chart/tx?output=csv"),
    HARMONY("Harmony", "ONEUSDT", "https://explorer.harmony.one/charts/tx"),
    APTOS("Aptos", "APTUSDT", "https://storage.googleapis.com/aptos-mainnet/explorer/chain_stats_v2.json?cache-version=0"),
    NEAR("Near", "NEARUSDT", "https://api.nearblocks.io/v1/charts"),
    CARDANO("Cardano", "ADAUSDT", "https://adastat.net/api/rest/v1/transactions.json?rows=true&sort=time&dir=desc&limit=24&page=1&currency=usd"),
    SUI("Sui", "SUIUSDT", "https://suiscan.xyz/api/sui-backend/mainnet/api/widgets/total-transactions?widgetPage=ANALYTICS&size=LARGE&period=24H"),
    SOLANA("Solana", "SOLUSDT", "https://public-api.solscan.io/chaininfo/");


    private final String name;
    private final String ticker;
    private final String url;

    Blockchain(String name, String ticker, String url) {
        this.name = name;
        this.ticker = ticker;
        this.url = url;
    }

}
