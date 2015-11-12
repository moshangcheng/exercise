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