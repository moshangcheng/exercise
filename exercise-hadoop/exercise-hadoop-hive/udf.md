# Hive UDF

Hive UDF分为：`UDF`、`GenericUDF`，`GenericUDF`更加强大

## `GenericUDF`

- It can accept arguments of complex types, and return complex types.
- It can accept variable length of arguments.
- It can accept an infinite number of function signature - for example, it's easy to write a GenericUDF that accepts `array<int>`, `array<array<int>>` and so on (arbitrary levels of nesting).
- It can do short-circuit evaluations. 

## 参考资料

- [Hive: GenericUDF and support of complex object](https://issues.apache.org/jira/browse/HIVE-45)
- [Writing a Hive Generic UDF](http://www.baynote.com/2012/11/a-word-from-the-engineers/)
- [挖挖Hive的代码（一）——UDF](http://blog.csdn.net/smartzxy/article/details/6726877)