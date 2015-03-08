# [Spark](https://spark.apache.org/)练习

## 介绍

`Spark`是一个流行的分布式内存计算框架。

通过设置master url，`Spark`能以如下模式运行：
- 本地模式
	- `--master local[*]`或者`new SparkConf().("local[*]")`
- [集群模式](http://spark.apache.org/docs/latest/cluster-overview.html)
	- [`standalone mode`](http://spark.apache.org/docs/latest/spark-standalone.html), spark运行于集群之上，不依赖于yarn和mesos
	- [`spark on YARN`](http://spark.apache.org/docs/latest/running-on-yarn.html)
	- [`spark on mesos`](http://spark.apache.org/docs/latest/running-on-mesos.html)

## 内容

## 任务


| 任务 | 开始日期 | 结束日期 | 进度 |
| --- | ---- | --- | --- |
| Spark Programming Guide | - | - | 0% |

## 参考资料

- [Spark internal - 多样化的运行模式](http://blog.csdn.net/colorant/article/details/18549027)
- [spark在哪里指定master URL](http://www.zhihu.com/question/23967309)
- [Spark examples](https://spark.apache.org/examples.html)
- [Spark Programming Guide](https://spark.apache.org/docs/latest/programming-guide.html)