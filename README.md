# Blockchain Transaction Tracker

## Overview

Blockchain Transaction Tracker is an application created for analyzing the correlation between blockchain transactions and the prices of their corresponding cryptocurrencies. The tool fetches transaction data, stores it in a database, selects transactions from the past few days, retrieves the daily price of the associated cryptocurrency, calculates the correlation, and then generates a heatmap chart illustrating the analysis results.
## Key Features

- Retrieval of transaction data from various blockchains.
- Recording transaction history in a database.
- Retrieval and analysis of daily cryptocurrency prices.
- Calculation of correlation between transactions and cryptocurrency prices.
- Generation of heatmap charts to visualize the correlation.

## Technologies

- Java (Spring Boot, JPA/Hibernate)
- Mysql
- Binance API ([binance-connector-java](https://github.com/binance/binance-connector-java))
- Charting library ([ECharts-Java](https://github.com/ECharts-Java/ECharts-Java))
