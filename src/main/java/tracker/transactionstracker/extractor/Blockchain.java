package tracker.transactionstracker.extractor;

import lombok.Getter;

@Getter
public enum Blockchain {
   ARBITRUM("Arbitrum", "https://arbiscan.io/chart/tx?output=csv"),
    AVALANCHE("Avalanche", "https://metrics.avax.network/v1/tx_count/mainnet?interval=day"),
    BSC("Bsc", "https://bscscan.com/chart/tx?output=csv"),
    COSMOS("Cosmos", "https://index.atomscan.com/blocks/average"),
    CRONOS("Cronos", "https://cronoscan.com/chart/tx?output=csv"),
    ETHEREUM("Ethereum", "https://etherscan.io/chart/tx?output=csv"),
    FANTOM("Fantom", "https://ftmscan.com/chart/tx?output=csv"),
    OPTIMISM("Optimism", "https://optimistic.etherscan.io/chart/tx?output=csv"),
    POLYGON("Polygon", "https://polygonscan.com/chart/tx?output=csv"),
    HARMONY("Harmony", "https://explorer.harmony.one/charts/tx"),
    APTOS("Aptos", "https://storage.googleapis.com/aptos-mainnet/explorer/chain_stats_v2.json?cache-version=0"),
    NEAR("Near", "https://api.nearblocks.io/v1/charts"),
    CARDANO("Cardano", "https://adastat.net/api/rest/v1/transactions.json?rows=true&sort=time&dir=desc&limit=24&page=1&currency=usd"),
   SUI("Sui", "https://suiscan.xyz/api/sui-backend/mainnet/api/widgets/total-transactions?widgetPage=ANALYTICS&size=LARGE&period=24H"),
     SOLANA("Solana", "https://public-api.solscan.io/chaininfo/");


    private final String name;
    private final String url;

    Blockchain(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
