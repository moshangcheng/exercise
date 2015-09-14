## [阿里移动推荐算法](http://tianchi.aliyun.com/competition/introduction.htm?spm=5176.100066.333.2.Fr5mqf&raceId=1)

## 数据分析

geo

```bash
$ cat action-origin.csv | cut -d ',' -f 4 | sort | uniq | wc -l
 582814
$ cat item.csv | cut -d ',' -f 2 | sort | uniq | wc -l
  45290
```

category

```bash
$ cat action-origin.csv | cut -d ',' -f 5 | sort | uniq | wc -l
   8899
$ cat item.csv | cut -d ',' -f 3 | sort | uniq | wc -l
    954
```

## 数据清洗

```bash
$ sort -t , +5 action-origin.csv | uniq > uniq-sort-action.csv
```

## JVM调优

```
-server -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -Xms2g -Xmx2g
```

```
positive action count: 107253
negative action count: 462032
feature count: 5487407
```