# SODD

## Introduction

SODD is an efficient solution for discovering set-based order dependencies (SODs) in relational data. Given a relational schema instance, SODD identifies all minimal valid SODs, which consist of constant order dependencies (CODs) and order-compatible dependencies (OCDs).

## Requirements

* Java 8 or later
* Maven 3.1.0 or later

## Usage

After building the project with Maven, you can obtain the `SODD.jar` file. You can run our code by providing 6 params. 

For example, you can run
```shell
java -jar SODD.jar ./Data/int/WP-21K-7.csv 10000000 1 1 1 1
```
This example processes the `WP-21K-7.csv` dataset, computes minimal valid SODs, and outputs the number of discovered dependencies and runtime statistics.

### Configures

If you want to run our code, you should input 6 params to specific your target.
```shell
java -jar SODD.jar <fp> <rowLimit> <k-1> <vioPairsThreshold> <rowRatio> <colRatio> 
```

- `fp`: The file path of the input dataset.
- `rowLimit`: The maximum number of rows to process from the dataset.
- `k`: A parameter for the validation process. Validate uses a min-heap to keep the top-k violating tuple pairs. It determines the number of high-priority violating pairs to retain for generating new non-OCDs.
- `vioPairsThreshold`: The threshold for the number of violating tuple pairs. When the count of violating pairs during OCD validation exceeds this threshold, new non-OCDs are generated, and the algorithm reverts to nExpand.
- `rowRatio`: The proportion of rows to use for scalability experiments (Experiment 2). For example, 0.1 means using 10% of the dataset rows.
- `colRatio`: The proportion of columns (attributes) to use for scalability experiments (Experiment 2). For example, 0.5 means using 50% of the total attributes.

## Comparative Experiments

SODD is compared to other two SOD discovery methods, FastOD and HyOD. The source code of FastOD can be found [here](https://git.io/fastodbid). The source code of HyOD can be found [here](https://github.com/jgszxlyh/HyOD/).

SODD is also compared with the functional dependency (FD) discovery algorithm HyFD and the lexicographical order dependency (LOD) discovery algorithm BOD. The source code of HyFD can be found [here](https://github.com/HPI-Information-Systems/metanome-algorithms/tree/master/HyFD). The source code of BOD can be found [here](https://github.com/chenjixuan20/BOD/).