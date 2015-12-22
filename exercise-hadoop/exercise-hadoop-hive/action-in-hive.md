# Hive实践

## 坑

- HiveQL中无法写子查询的问题
	- `Unsupported SubQuery Expression 'c1': Correlating expression cannot contain unqualified column references`
	- 这是Hive的[一个Bug](https://issues.apache.org/jira/browse/HIVE-9734)，解决方案为在子查询中使用别名
- 使用带参数的`unix_timestamp()`函数作为分区字段过滤条件时，Hive无法做编译优化，会扫描全表而不是指定分区
	- 因为`unix_timestamp`是一个非确定性函数，其值可能动态变化
	- 解决方案
		- 使用`to_unix_timestamp`代替
		- [Hive-3853](https://issues.apache.org/jira/browse/HIVE-3853)和[Hive-1986](https://issues.apache.org/jira/browse/HIVE-1986)
- Hive的`join`
	- 尽量先过滤，在join，这样不管是inner join还是outer join，都不会有问题
	- 对于outer join，如果先join，再过滤，结果是不正确的
- [Hive-11271 java.lang.IndexOutOfBoundsException when union all](https://issues.apache.org/jira/browse/HIVE-11271)
	- `union all`的表中如果有条件查询，可能会出现数组越界异常
	- 规避方案是关闭下推优化`set hive.optimize.ppd=false;`