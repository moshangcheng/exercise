# Redis的数据导入和导出

## 单个Key

使用`dump`和`restore`命令

`redis-cli --raw dump test | head -c-1 | redis-cli -x restore test1 0`

参考资料

- [How to use redis' `DUMP` and `RESTORE` (offline)?](http://stackoverflow.com/questions/16127682/how-to-use-redis-dump-and-restore-offline/16129052#16129052)

## 数据库的导入导出
