package tracker.transactionstracker.extractor;

import lombok.Getter;

@Getter
public enum Blockchain {
    ARBITRUM("Arbitrum", "arb", "https://arbiscan.io/chart/tx?output=csv"),
    AVALANCHE("Avalanche", "avax", "https://metrics.avax.network/v1/tx_count/mainnet?interval=day"),
    BSC("Bsc", "bsc", "https://bscscan.com/chart/tx?output=csv"),
    COSMOS("Cosmos", "atom", "https://index.atomscan.com/blocks/average"),
    CRONOS("Cronos", "cro", "https://cronoscan.com/chart/tx?output=csv"),
    ETHEREUM("Ethereum", "eth", " https://etherscan.io/chart/tx?output=csv"),
    FANTOM("Fantom", "ftm", "https://ftmscan.com/chart/tx?output=csv"),
    OPTIMISM("Optimism", "op", "https://optimistic.etherscan.io/chart/tx?output=csv"),
    POLYGON("Polygon", "matic", "https://polygonscan.com/chart/tx?output=csv"),
    HARMONY("Harmony", "one", "https://explorer.harmony.one/charts/tx"),
    APTOS("Aptos", "apt", "https://storage.googleapis.com/aptos-mainnet/explorer/chain_stats_v2.json?cache-version=0"),
    NEAR("Near", "near", "https://api.nearblocks.io/v1/charts"),
    CARDANO("Cardano", "ada", "https://adastat.net/api/rest/v1/transactions.json?rows=true&sort=time&dir=desc&limit=24&page=1&currency=usd"),
    SUI("Sui", "sui", "https://suiscan.xyz/api/sui-backend/mainnet/api/widgets/total-transactions?widgetPage=ANALYTICS&size=LARGE&period=24H"),
    SOLANA("Solana", "sol", "https://public-api.solscan.io/chaininfo/");


    private final String name;
    private final String tokenName;
    private final String url;

    Blockchain(String name, String tokenName, String url) {
        this.name = name;
        this.tokenName = tokenName;
        this.url = url;
    }
}
