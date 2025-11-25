#!/bin/bash

datasets=(
    "Adult-32K-15"
    "ALP-20K-17"
    "ATH-280K-11"
    "DB-250K-30"
    "EQ-500K-12"
    "FDR-250K-15"
    "FEB-500K-15"
    "FLI-500K-17"
    "Fuel-22K-6"
    "Letter-20K-17"
    "NCV-500K-9"
    "NUT-80K-15"
    "SAL-150K-9"
    "VEH-300K-16"
    "WP-21K-7"
)

jar1="SODDM.jar"
output_file_SODD="results_SODDM_int.txt"
> $output_file_SODD

for dataset in "${datasets[@]}"; do
    csv_file="Data/int/${dataset}.csv"

    echo "Processing $csv_file with $jar1..."
    result1=$(java -Xms2g -Xmx80g -jar $jar1 $csv_file 10000000 1 1 1 1)
    echo "Jar: $jar1, Dataset: $csv_file, Result: $result1" >> $output_file_SODD
done


echo "Datasets(int) processing completed. Results saved in $output_file_SODD."

