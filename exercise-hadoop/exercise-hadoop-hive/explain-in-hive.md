# HiveQL执行计划

### 介绍

一个map/reduce stage有两个部分：

- map，从Hive表或上一个stage读取输入；如果从上一个stage读取输入，需要指定key、value类型、排序方式和分区方式等，以及相关统计信息
- reduce，进行具体的操作，可能会进行部分聚合

参考资料

- [LanguageManual Explain](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+Explain)
- [hive sql执行计划树解析](http://blog.csdn.net/moon_yang_bj/article/details/17230253)

### 实例分析

对于如下的HiveQL语句

```sql
select
  uid, concat_ws(',', collect_list(cast(iid as string))) as iids
from
(
  select
    recom.uid, recom.pos, recom.iid
  from
  (
    select uid, pos, iid from bbalgo.lda_recom_per_temp lateral view posexplode(iids) t_ as pos, iid
  ) recom
  join
  (
    select id, cid, event_id from bbdw.db_item where pt='20151125'
  ) item
  on recom.iid = item.id
  order by recom.pos
) t group by uid
```

执行`explain`命令的输出为:

```
Explain
STAGE DEPENDENCIES:
  Stage-1 is a root stage
  Stage-2 depends on stages: Stage-1
  Stage-3 depends on stages: Stage-2
  Stage-0 depends on stages: Stage-3
```

忽略Stage-1和Stage-3，重点观察Stage-2

- order by是全局排序，只有一个Reducer，从Stage-2的Map Operator没有`Map-reduce partition columns`，没有进行分区，可以得到验证
- `sort order: +`表示按照key进行升序排序

```
Stage: Stage-2
  Map Reduce
    Map Operator Tree:
        TableScan
          Reduce Output Operator
            key expressions: _col1 (type: int)
            sort order: +
            Statistics: Num rows: 120263625 Data size: 962109003 Basic stats: COMPLETE Column stats: NONE
            value expressions: _col0 (type: bigint), _col2 (type: bigint)
    Reduce Operator Tree:
      Select Operator
        expressions: VALUE._col0 (type: bigint), VALUE._col1 (type: bigint)
        outputColumnNames: _col0, _col2
        Statistics: Num rows: 120263625 Data size: 962109003 Basic stats: COMPLETE Column stats: NONE
        Group By Operator
          aggregations: collect_list(UDFToString(_col2))
          keys: _col0 (type: bigint)
          mode: hash
          outputColumnNames: _col0, _col1
          Statistics: Num rows: 120263625 Data size: 962109003 Basic stats: COMPLETE Column stats: NONE
          File Output Operator
            compressed: false
            table:
                input format: org.apache.hadoop.mapred.SequenceFileInputFormat
                output format: org.apache.hadoop.hive.ql.io.HiveSequenceFileOutputFormat
                serde: org.apache.hadoop.hive.serde2.lazybinary.LazyBinarySerDe
```

如果使用`distribute by recom.uid sort by recom.pos`代替`order by`，即

```sql
select
  uid, concat_ws(',', collect_list(cast(iid as string))) as iids
from
(
  select
    recom.uid, recom.pos, recom.iid
  from
  (
    select uid, pos, iid from bbalgo.lda_recom_per_temp lateral view posexplode(iids) t_ as pos, iid
  ) recom
  join
  (
    select id, cid, event_id from bbdw.db_item where pt='20151125'
  ) item
  distribute by recom.uid sort by recom.pos
) t group by uid
```

执行`explain`的输出为：

```
Explain
STAGE DEPENDENCIES:
  Stage-1 is a root stage
  Stage-2 depends on stages: Stage-1
  Stage-3 depends on stages: Stage-2
  Stage-0 depends on stages: Stage-3
```

忽略Stage-1和Stage-3，重点观察Stage-2：

- 按照`_col0`即uid进行分区，按照`_col1`即pos进行排序

```
Stage: Stage-2
  Map Reduce
    Map Operator Tree:
        TableScan
          Reduce Output Operator
            key expressions: _col1 (type: int)
            sort order: +
            Map-reduce partition columns: _col0 (type: bigint)
            Statistics: Num rows: 120263625 Data size: 962109003 Basic stats: COMPLETE Column stats: NONE
            value expressions: _col0 (type: bigint), _col2 (type: bigint)
    Reduce Operator Tree:
      Select Operator
        expressions: VALUE._col0 (type: bigint), VALUE._col1 (type: bigint)
        outputColumnNames: _col0, _col2
        Statistics: Num rows: 120263625 Data size: 962109003 Basic stats: COMPLETE Column stats: NONE
        Group By Operator
          aggregations: collect_list(UDFToString(_col2))
          keys: _col0 (type: bigint)
          mode: hash
          outputColumnNames: _col0, _col1
          Statistics: Num rows: 120263625 Data size: 962109003 Basic stats: COMPLETE Column stats: NONE
          File Output Operator
            compressed: false
            table:
                input format: org.apache.hadoop.mapred.SequenceFileInputFormat
                output format: org.apache.hadoop.hive.ql.io.HiveSequenceFileOutputFormat
                serde: org.apache.hadoop.hive.serde2.lazybinary.LazyBinarySerDe
```

进一步优化，使用`distribute by recom.uid sort by recom.uid, recom.pos`代替`order by`，即

```sql
select
  uid, concat_ws(',', collect_list(cast(iid as string))) as iids
from
(
  select
    recom.uid, recom.pos, recom.iid
  from
  (
    select uid, pos, iid from bbalgo.lda_recom_per_temp lateral view posexplode(iids) t_ as pos, iid
  ) recom
  join
  (
    select id, cid, event_id from bbdw.db_item where pt='20151125'
  ) item
  distribute by recom.uid sort by recom.uid, recom.pos
) t group by uid
```

执行`explain`的输出为：

```
STAGE DEPENDENCIES:
  Stage-1 is a root stage
  Stage-2 depends on stages: Stage-1
  Stage-0 depends on stages: Stage-2
```

只有两个Stage，Stage-3已经被Hive优化了:

- 按照`_col0`即uid进行分区，按照`_col0`即uid、`_col1`即pos进行排序

```
Stage: Stage-2
  Map Reduce
    Map Operator Tree:
        TableScan
          Reduce Output Operator
            key expressions: _col0 (type: bigint), _col1 (type: int)
            sort order: ++
            Map-reduce partition columns: _col0 (type: bigint)
            Statistics: Num rows: 22137581 Data size: 3719113808 Basic stats: COMPLETE Column stats: NONE
            value expressions: _col2 (type: bigint)
    Reduce Operator Tree:
      Select Operator
        expressions: KEY.reducesinkkey0 (type: bigint), VALUE._col0 (type: bigint)
        outputColumnNames: _col0, _col2
        Statistics: Num rows: 22137581 Data size: 3719113808 Basic stats: COMPLETE Column stats: NONE
        Group By Operator
          aggregations: collect_list(UDFToString(_col2))
          keys: _col0 (type: bigint)
          mode: complete
          outputColumnNames: _col0, _col1
          Statistics: Num rows: 11068790 Data size: 1859556819 Basic stats: COMPLETE Column stats: NONE
          Select Operator
            expressions: _col0 (type: bigint), concat_ws(',', _col1) (type: string)
            outputColumnNames: _col0, _col1
            Statistics: Num rows: 11068790 Data size: 1859556819 Basic stats: COMPLETE Column stats: NONE
            File Output Operator
              compressed: false
              Statistics: Num rows: 11068790 Data size: 1859556819 Basic stats: COMPLETE Column stats: NONE
              table:
                  input format: org.apache.hadoop.mapred.TextInputFormat
                  output format: org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat
                  serde: org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe
```

考虑使用窗口函数

```sql
select
  uid, concat_ws(',', collect_list(cast(iid as string))) as iids
from
(
  select
    recom.uid, recom.iid, row_number() over(partition by recom.uid order by recom.pos)
  from
  (
    select uid, pos, iid from bbalgo.lda_recom_per_temp lateral view posexplode(iids) t_ as pos, iid
  ) recom
  join
  (
    select id, cid, event_id from bbdw.db_item where pt='20151125'
  ) item
) t group by uid
```

执行`explain`的输出为：

```
STAGE DEPENDENCIES:
  Stage-1 is a root stage
  Stage-2 depends on stages: Stage-1
  Stage-0 depends on stages: Stage-2
```

```
Stage: Stage-2
  Map Reduce
    Map Operator Tree:
        TableScan
          Reduce Output Operator
            key expressions: _col0 (type: bigint), _col1 (type: int)
            sort order: ++
            Map-reduce partition columns: _col0 (type: bigint)
            Statistics: Num rows: 22137581 Data size: 3719113808 Basic stats: COMPLETE Column stats: NONE
            value expressions: _col2 (type: bigint)
    Reduce Operator Tree:
      Select Operator
        expressions: KEY.reducesinkkey0 (type: bigint), KEY.reducesinkkey1 (type: int), VALUE._col0 (type: bigint)
        outputColumnNames: _col0, _col1, _col2
        Statistics: Num rows: 22137581 Data size: 3719113808 Basic stats: COMPLETE Column stats: NONE
        PTF Operator
          Statistics: Num rows: 22137581 Data size: 3719113808 Basic stats: COMPLETE Column stats: NONE
          Select Operator
            expressions: _col0 (type: bigint), _col2 (type: bigint)
            outputColumnNames: _col0, _col1
            Statistics: Num rows: 22137581 Data size: 3719113808 Basic stats: COMPLETE Column stats: NONE
            Group By Operator
              aggregations: collect_list(UDFToString(_col1))
              keys: _col0 (type: bigint)
              mode: hash
              outputColumnNames: _col0, _col1
              Statistics: Num rows: 22137581 Data size: 3719113808 Basic stats: COMPLETE Column stats: NONE
              File Output Operator
                compressed: false
                table:
                    input format: org.apache.hadoop.mapred.SequenceFileInputFormat
                    output format: org.apache.hadoop.hive.ql.io.HiveSequenceFileOutputFormat
                    serde: org.apache.hadoop.hive.serde2.lazybinary.LazyBinarySerDe
```