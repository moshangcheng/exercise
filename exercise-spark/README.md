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

子目录

- [Spark基础](./exercise-spark-basic)
- [Spark工具类](./exercise-spark-util)
- [MLLib练习](./exercise-spark-mllib)
- [GraphX练习](./exercise-spark-graphx)
- [Spark Streaming练习](./exercise-spark-streaming)
- [Spark SQL练习](./exercise-spark-sql)
- [Spark Hive练习](./exercise-spark-hive)

其它

- [Spark Programming Guide笔记](./Spark-Programming-Guide-notes.md)

## 任务

| 任务 | 开始日期 | 结束日期 | 进度 |
| --- | ---- | --- | --- |
| Spark Programming Guide | - | - | 0% |

## 参考资料

- [Spark internal - 多样化的运行模式](http://blog.csdn.net/colorant/article/details/18549027)
- [spark在哪里指定master URL](http://www.zhihu.com/question/23967309)
- [Spark examples](https://spark.apache.org/examples.html)
- [Spark Programming Guide](https://spark.apache.org/docs/latest/programming-guide.html)
- [spark八法之方圆:RDD](https://github.com/shijinkui/spark_study/blob/master/spark_eight_style_rdd.markdown)
- [pyspark原理简介](http://blog.csdn.net/pelick/article/details/38307631)
- [How-to: Use IPython Notebook with Apache Spark](http://blog.cloudera.com/blog/2014/08/how-to-use-ipython-notebook-with-apache-spark/)