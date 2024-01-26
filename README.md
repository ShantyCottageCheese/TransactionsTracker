# Blockchain Transaction Tracker

## Overview

Blockchain Transaction Tracker is an application created for analyzing the correlation between blockchain transactions and the prices of their corresponding cryptocurrencies. The tool fetches transaction data, stores it in a database, selects transactions from the past few days, retrieves the daily price of the associated cryptocurrency, calculates the correlation, and then generates a heatmap chart illustrating the analysis results.

## Supported Blockchains
- Arbitrum (ARB)
- Avalanche (AVAX)
- Binance-Smart-Chain (BNB)
- Cosmos (ATOM)
- Ethereum (ETH)
- Fantom (FTM)
- Optimism (OP)
- Polygon (MATIC)
- Harmony (ONE)
- Aptos (APT)
- Near (NEAR)
- Cardano (ADA)
- Sui (SUI)
- Solana (SOL)

## Key Features
- Retrieval of transaction data from various blockchains.
- Recording transaction history in a database.
- Retrieval and analysis of daily cryptocurrency prices.
- Calculation of correlation between transactions and cryptocurrency prices.
- Generation of heatmap charts to visualize the correlation.

## Available endpoint
- `POST /save-data`
- `GET /health`
- `GET /heatmap?days=<numberOfDays>`

## Technologies
- Java (Spring Boot, JPA)
- Mysql
- Binance API ([binance-connector-java](https://github.com/binance/binance-connector-java))
- Charting library ([ECharts-Java](https://github.com/ECharts-Java/ECharts-Java))

## Remember
Before running the application, make sure you have configured the `application.properties` file with the appropriate data.