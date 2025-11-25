# Exp-6 Strategies for non-OCD generation

## Initial generation
|  File  |  Description  |
|  ----  | ----  |
| `random-50.zip` | Source code for the initial non-OCD generation strategy using 50 tuples randomly sampled from datasets.|
| `random-100.zip` | Source code for the initial non-OCD generation strategy using 100 tuples randomly sampled from datasets.|
| `cluster-2.zip` | Source code for the initial non-OCD generation strategy using 2 tuples sampled from each cluster.|
| `cluster-4.zip` | Source code for the initial non-OCD generation strategy using 2 tuples sampled from each cluster.|

## Augmentation
|  File  |  Description  |
|  ----  | ----  |
| `first-k.zip` | Source code for the augmentation strategy that collects the first k encountered violating tuple pair during validation.|
| `top-k.zip` | Source code for the augmentation strategy that selects the top k violating tuple pairs with the highest number of shared attribute values during validation.|

You can modify `k` by changing the third parameter in the shell command.
```shell
java -jar first-k.jar <fp> <rowLimit> <k> <vioPairsThreshold> <rowRatio> <colRatio> 
java -jar top-k.jar <fp> <rowLimit> <k-1> <vioPairsThreshold> <rowRatio> <colRatio> 
``` 

For example, you can run the first-1, first-10, top-2, and top-10 strategies.
```shell
java -jar first-k.jar ./Data/int/WP-21K-7.csv 10000000 1 1 1 1
java -jar first-k.jar ./Data/int/WP-21K-7.csv 10000000 10 1 1 1
java -jar top-k.jar ./Data/int/WP-21K-7.csv 10000000 1 1 1 1
java -jar top-k.jar ./Data/int/WP-21K-7.csv 10000000 9 1 1 1
```

## Impact of initial non-OCDs
|  File  |  Description  |
|  ----  | ----  |
| `nosample.zip` | Source code for a version without GennOCD.|

You can run the strategies with `exp-sample.sh`.

