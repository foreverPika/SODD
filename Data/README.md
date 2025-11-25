# Datasets

|  Name  |  Source  |
|  ----  | ----  |
|  Adult  | [hpi(https://hpi.de/naumann/projects/repeatability/data-profiling/fds.html)](https://hpi.de/naumann/projects/repeatability/data-profiling/fds.html)  |
|  ALP(letter-sub)  |  [hpi](https://hpi.de/naumann/projects/repeatability/data-profiling/fds.html)  |
|  ATH(athletes)  |  https://www.kaggle.com/datasets/nitishsharma01/olympics-124-years-datasettill-2020  |
|  DB(DB status)  |  [hpi](https://hpi.de/naumann/projects/repeatability/data-profiling/fds.html)  |
|  DBLP  |  [hpi](https://hpi.de/naumann/projects/repeatability/algorithms/distod.html)  |
|  EQ(Earthquake)  |  [https://www.kaggle.com/datasets/farazrahman/earthquake](https://www.kaggle.com/datasets/farazrahman/earthquake)  |
|  FDR  |  [hpi](https://hpi.de/naumann/projects/repeatability/data-profiling/fds.html)  |
|  FEB(February Flight Delay Prediction)  |  https://www.kaggle.com/datasets/divyansh22/february-flight-delay-prediction?select=Feb_2020_ontime.csv  |
|  FLI(flight)  |  [hpi](https://hpi.de/naumann/projects/repeatability/data-profiling/fds.html)  |
|  Fuel(Fuel Consumption)  |  [https://www.kaggle.com/datasets/ahmettyilmazz/fuel-consumption](https://www.kaggle.com/datasets/ahmettyilmazz/fuel-consumption)  |
|  Letter(letter)  |  [hpi](https://hpi.de/naumann/projects/repeatability/data-profiling/fds.html)  |
|  NCV(ncvoter)  |  [hpi](https://hpi.de/naumann/projects/repeatability/data-profiling/fds.html)  |
|  NCV1M(ncvoter)  |  [hpi](https://hpi.de/naumann/projects/repeatability/algorithms/distod.html)  |
|  NCV4M(ncvoter)  |  [hpi](https://hpi.de/naumann/projects/repeatability/algorithms/distod.html)  |
|  NUT(Nutrition, Physical Activity, and Obesity)  |  https://www.kaggle.com/datasets/mattop/nutrition-physical-activity-and-obesity  |
|  SAL(SF Salaries)  |  https://www.kaggle.com/datasets/kaggle/sf-salaries  |
|  VEH(Used Cars Dataset)  |  https://www.kaggle.com/datasets/austinreese/craigslist-carstrucks-data  |
|  WP(world population)  |  https://www.kaggle.com/datasets/iamsouravbanerjee/world-population-dataset/data  |

## Integer Datasets

We utilize several integer-type datasets (with data in each `int/*.csv` file being of integer type) used in FastOD and HyOD experiments. All integer-type datasets are stored in the `int` folder. We perform preprocessing on the datasets to convert attribute values into integers without altering the original order of values. This preprocessing strategy is consistent with that adopted by prior works such as [HyOD](https://doi.org/10.1109/ICDE60146.2024.00059) and [FastOD](https://doi.org/10.1007/s00778-018-0510-0).

## Long Datasets

Three long-type datasets (with data in each `long/*.csv` file being of long type) applied in the [DISTOD](https://hpi.de/naumann/projects/repeatability/algorithms/distod.html) experiments are included: DBLP, NCV1M, and NCV4M. NCV1M is provided in the `long` folder, while DBLP and NCV4M are not directly provided here due to their large file sizes. Please download them from the corresponding links specified in the dataset table above. 

To support the execution of SODD on long-type datasets, we develop another version of SODD called SODDL. The source code of SODDL is provided in the package `SODDL.zip`.