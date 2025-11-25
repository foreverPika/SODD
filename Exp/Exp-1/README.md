# Exp-1 Algorithm comparison

This directory contains scripts to run SODD algorithm on two types of datasets:
- Integer datasets: Located in `Data/int/`
- Long datasets: Located in `Data/long/`

## Scripts Overview

|  File  |  Description  |
|  ----  | ----  |
| `int.sh` | Executes `SODD.jar` on all integer datasets in `Data/int/` with default parameters. |
| `long.sh` | Executes `SODDL.jar` on all long datasets in `Data/long/` with default parameters. |
| `exp-1.sh` | Sequentially executes `int.sh` and `long.sh` to process integer and long datasets respectively. |
