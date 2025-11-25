# Exp-7 Parameters
|  File  |  Description  |
|  ----  | ----  |
| `exp-para.zip` | code for different parameters.|

You can modify `maxFails` by changing the seventh parameter in the shell command.
```shell
java -jar SODD.jar <fp> <rowLimit> <k> <vioPairsThreshold> <rowRatio> <colRatio> <maxFails>
``` 

You can modify `k` by changing the third parameter in the shell command.
```shell
java -jar SODD.jar <fp> <rowLimit> <k> <vioPairsThreshold> <rowRatio> <colRatio> <maxFails>
``` 

For example, you can run
```shell
java -jar SODD.jar ./Data/int/WP-21K-7.csv 10000000 1 1 1 1 1
```

You can run all parameters with `exp-para.sh`.
