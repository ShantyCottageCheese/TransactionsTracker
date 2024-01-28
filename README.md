# Blockchain Transaction Tracker

## Overview

Blockchain Transaction Tracker is an application created for analyzing the correlation between blockchain transactions and the prices of their corresponding cryptocurrencies. The tool fetches transaction data, stores it in a database, selects transactions from the past few days, retrieves the daily price of the associated cryptocurrency, calculates the correlation, and then generates a heatmap chart illustrating the analysis results.

## Key Features
- Retrieval of transaction data from various blockchains.
- Recording transaction history in a database.
- Retrieval and analysis of daily cryptocurrency prices.
- Calculation of correlation between transactions and cryptocurrency prices.
- Generation of heatmap charts to visualize the correlation.

## Installation and usage
Before running the application, make sure you have configured the `application.properties` file with the appropriate data. Additionally, it is recommended to map the `TransactionEntity` to the corresponding table in the database.
- `git clone https://github.com/ShantyCottageCheese/TransactionsTracker.git`
- `cd TransactionTracker`
- `./mvnw clean install`
- `./mvnw spring-boot:run`

The application will be accessible at http://localhost:8080.

### **Prerequisites: Java 21**
## Available endpoint
- `POST /saveData`
- `GET /health`
- `GET /heatmap?days=<numberOfDays>`

The `saveData` method allows for manual retrieval of data from various API sources and saves them into the database. 
Additionally, there is an option to configure a scheduler that will automatically trigger this operation at specified time intervals.

The `health` endpoint is used to check the health status of the application. Calling this endpoint will return a status of OK (200), indicating that the application is functioning correctly.

The `heatmap` endpoint is used for generating heatmap charts that visualize correlation based on historical data. The days parameter allows specifying the number of days to consider in the generated chart.

To generate a heatmap, make an HTTP GET request to the /heatmap endpoint with the days' parameter.

![image](https://github.com/ShantyCottageCheese/TransactionsTracker/assets/110695928/be95300c-afc3-453b-afee-673171e6374c)


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


## Technologies
- Java (Spring Boot, JPA)
- Mysql
- Binance API library ([binance-connector-java](https://github.com/binance/binance-connector-java))
- Charting library ([ECharts-Java](https://github.com/ECharts-Java/ECharts-Java))
