# Exp-4 Discovery methods for related dependencies

## Result comparison

All LODs discovered by BOD can typically be represented by a small number of SODs. This is because many LODs correspond to different combinations of fundamental SODs. The `BOD.zip` package contains code for converting LODs obtained from BODs into SODs and calculating their proportion relative to the total number of SODs.

If you want to run BOD, you should input 2 params.
```shell
java -jar BOD.jar <fp> <sodResult> 
```
- `fp`: The file path of the input dataset.
- `sodResult`: The file path of the txt file that stores all SOD results discovered by SODD. The txt file can be obtained from `Exp-5/SODD-txt.zip.`.

For example, you can run
```shell
java -jar BOD.jar ./Data/int/WP-21K-7.csv ./Data/sod_result/sod-WP-21K-7-result.txt
```
