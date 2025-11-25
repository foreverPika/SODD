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
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 0.1 1)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 0.2 1)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 0.3 1)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 0.4 1)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 0.5 1)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 0.6 1)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 0.7 1)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 0.8 1)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 0.9 1)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 1 0.2)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 1 0.4)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 1 0.6)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 1 0.8)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 1 1)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODDL
done


echo "Datasets(long) processing completed. Results saved in $output_file_SODDL."
