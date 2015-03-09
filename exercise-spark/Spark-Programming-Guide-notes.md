# [Spark Programming Guide](https://spark.apache.org/docs/latest/programming-guide.html)笔记

## Overview

每个Spark应用程序包含一个驱动程序用于执行用户的`main`函数以及在集群上执行各种并行操作。

Spark中的RDD(Resilient Distributed Memory)
- 一个分布于各个节点上、可被并行操作的数据集
- 可从HDFS中文件中创建，可以从已有的Scala集合中创建
- 可持久化到内存中以备重用
- 如果节点失败会自动重建

Spark中的共享变量(Shared Variable)
- 当执行某个函数时，Spark会在多个节点上运行许多任务，它默认会将函数中的变量复制到每个任务
- 如果某个变量需要被多个任务共享，可以使用Spark提共享变量，有两种类型
	- *broadcast variables*用于在所有节点的内存中存储一个值
	- *accumulators*用于只支持`add`操作的情形

## Initializing Spark

*SparkConf*告诉Spark如何连接集群，通过*SparkConf*可以创建*SparkContext*。

	val conf = new SparkConf().setAppName("AppName").setMaster("local[*]")
	val spark = new SparkContext(conf)

对于每个JVM,*SparkContext*必须是唯一的。在创建新的*SparkContext*前，必须调用`stop()`方法停止旧的*SparkContext*。

通过`spark-submit`提交Spark应用的时候，可以通过`--master`参数指定集群地址。

## Resilient Distributed Datasets(RDDs)