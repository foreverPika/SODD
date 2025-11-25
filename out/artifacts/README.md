# Jar

## Introduction

This directory provides four JAR files for executing the SODD algorithm on different datasets, including both single-threaded and multi-threaded versions. Below is a concise introduction to each JAR file, along with their usage instructions.

## Requirements

* Java 8 or later

## JAR File

|  File  |  Description  |
|  ----  | ----  |
| `SODD.jar` | Single-threaded SODD algorithm for integer datasets. |
| `SODDL.jar` | Single-threaded SODD algorithm optimized for long-type datasets. |

## Usage

All JAR files can be executed using the following command format:
```shell
java -jar <jarFileName> <fp> <rowLimit> <k-1> <vioPairsThreshold> <rowRatio> <colRatio> 
```
- `jarFileName`: The name of the JAR file you want to run (`SODD.jar`, `SODDL.jar`).
- `fp`: The file path of the input dataset.
- `rowLimit`: The maximum number of rows to process from the dataset.
- `k`: A parameter for the validation process. Validate uses a min-heap to keep the top-k violating tuple pairs. It determines the number of high-priority violating pairs to retain for generating new non-OCDs.
- `vioPairsThreshold`: The threshold for the number of violating tuple pairs. When the count of violating pairs during OCD validation exceeds this threshold, new non-OCDs are generated, and the algorithm reverts to nExpand.
- `rowRatio`: The proportion of rows to use for scalability experiments (Experiment 2). For example, 0.1 means using 10% of the dataset rows.
- `colRatio`: The proportion of columns (attributes) to use for scalability experiments (Experiment 2).For example, 0.5 means using 50% of the total attributes.
For example, you can run
```shell
java -jar SODD.jar ./Data/int/WP-21K-7.csv 10000000 1 1 1 1
```