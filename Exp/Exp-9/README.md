# Exp-9 Parallelism

This directory stores the source codes of the multi-threaded versions of SODD, along with scripts that facilitate running the multi-threaded SODD algorithm on two specific types of datasets:
- Integer datasets: Located in `Data/int/`
- Long datasets: Located in `Data/long/`

## Multi-threaded Source Codes

|  File  |  Description  |
|  ----  | ----  |
| `SODDM.zip` | Source code for the multi-threaded version of SODD for integer datasets. It has a default of 8 threads, and you can modify the thread count in the `SODDM.java`. |
| `SODDML.zip` | Source code for the multi-threaded version of SODD for long-type datasets. It has a default of 8 threads, and you can modify the thread count in the `SODDML.java`. |

## Scripts Overview

|  File  |  Description  |
|  ----  | ----  |
| `int.sh.zip` | Executes `SODDM.jar` on all integer datasets in `Data/int/` with default parameters. |
| `long.sh` | Executes `SODDML.jar` on all long datasets in `Data/long/` with default parameters. |
| `exp-9.sh` | Sequentially executes `int.sh` and `long.sh` to process integer and long datasets respectively. |
