# Exp-2 Scalability

This directory contains scripts to evaluate the scalability of the SODD algorithm on two types of datasets:
- Integer datasets: Located in `Data/int/`
- Long datasets: Located in `Data/long/`

## Scripts Overview

|  File  |  Description  |
|  ----  | ----  |
| `int.sh` | Executes `SODD.jar` on integer datasets in `Data/int/` with row ratios (0.1, 0.2, ..., 1.0) and column ratios (0.2, 0.4, 0.6, 0.8, 1.0). |
| `long.sh` | Executes `SODDL.jar` on long datasets in `Data/long/` with row ratios (0.1, 0.2, ..., 1.0) and column ratios (0.2, 0.4, 0.6, 0.8, 1.0). |
| `exp-2.sh` | Sequentially executes `int.sh` and `long.sh` to process integer and long datasets respectively. |