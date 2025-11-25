#!/bin/bash

datasets=(
    "DBLP-8M-13"
    "NCV-1M-19"
    "NCV-4M-19"
)

jar1="SODDL.jar"
output_file_SODDL="results_SODDL_long.txt"
> $output_file_SODDL

for dataset in "${datasets[@]}"; do
    csv_file="Data/long/${dataset}.csv"

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 1 1)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL
done


echo "Datasets(long) processing completed. Results saved in $output_file_SODDL."
