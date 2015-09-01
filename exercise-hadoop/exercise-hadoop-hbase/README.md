# HBase

## 内容

## 安装单节点HBase

- 下载解压HBase
- 执行`./bin/start-hbase.sh`，启动zookeeper和hbase
- 执行`./bin/hbase shell`，进入hbase shell

测试发现只能在Linux上运行

## HBase Shell命令

`list`，显示所有表；`list 't.*'`显示所有以`t`开头的字符串

`status`，显示集群状态

`desc 't'`，获得一个表的描述信息

`create 't', 'cf1', 'cf2'`，创建一个有两个列族的表



## 参考资料

- [HBase Quick Start](http://hbase.apache.org/0.94/book/quickstart.html) 
- [HBase shell commands](https://learnhbase.wordpress.com/2013/03/02/hbase-shell-commands/)
- [HBase MapReduce Examples](http://hbase.apache.org/0.94/book/mapreduce.example.html)
- [HBase coprocessor introduction](https://blogs.apache.org/hbase/entry/coprocessor_introduction)
- [HBase Coprocessor 剖析与编程实践](http://www.cnblogs.com/ventlam/archive/2012/10/30/2747024.html)
- [HBase高性能复杂条件查询引擎](http://www.infoq.com/cn/articles/hbase-second-index-engine)
- [让你明白hbase二级索引核心思想](http://www.aboutyun.com/thread-8819-1-1.html)
- [Hbase系统架构及数据结构](http://www.uml.org.cn/zjjs/201211132.asp)