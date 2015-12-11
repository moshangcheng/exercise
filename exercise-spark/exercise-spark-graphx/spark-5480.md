# Spark-5480

跑LDA的时候，如果数据量比较大，经常遇到这个问题

```
java.lang.ArrayIndexOutOfBoundsException: -1
        at org.apache.spark.graphx.util.collection.GraphXPrimitiveKeyOpenHashMap$mcJI$sp.apply$mcJI$sp(GraphXPrimitiveKeyOpenHashMap.scala:64)
        at org.apache.spark.graphx.impl.EdgePartition.updateVertices(EdgePartition.scala:91)
        at org.apache.spark.graphx.impl.ReplicatedVertexView$$anonfun$2$$anonfun$apply$1.apply(ReplicatedVertexView.scala:75)
        at org.apache.spark.graphx.impl.ReplicatedVertexView$$anonfun$2$$anonfun$apply$1.apply(ReplicatedVertexView.scala:73)
        at scala.collection.Iterator$$anon$11.next(Iterator.scala:328)
        at scala.collection.Iterator$$anon$13.hasNext(Iterator.scala:371)
        at org.apache.spark.shuffle.sort.BypassMergeSortShuffleWriter.insertAll(BypassMergeSortShuffleWriter.java:99)
        at org.apache.spark.shuffle.sort.SortShuffleWriter.write(SortShuffleWriter.scala:73)
        at org.apache.spark.scheduler.ShuffleMapTask.runTask(ShuffleMapTask.scala:73)
        at org.apache.spark.scheduler.ShuffleMapTask.runTask(ShuffleMapTask.scala:41)
        at org.apache.spark.scheduler.Task.run(Task.scala:88)
        at org.apache.spark.executor.Executor$TaskRunner.run(Executor.scala:214)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
        at java.lang.Thread.run(Thread.java:745)
```

经过调查，发现这是GraphX的一个bug

- [JIRA: Spark-5480](https://issues.apache.org/jira/browse/SPARK-5480) GraphX pageRank: java.lang.ArrayIndexOutOfBoundsException
- [Stackoverflow: Apache Spark GraphX java.lang.ArrayIndexOutOfBoundsException](http://stackoverflow.com/questions/30028626/apache-spark-graphx-java-lang-arrayindexoutofboundsexception)
- [Spark Bug: Counting twice with different results](http://apache-spark-developers-list.1001551.n3.nabble.com/Spark-Bug-Counting-twice-with-different-results-td12391.html)
- [Apache Spark GraphX java.lang.ArrayIndexOutOfBoundsException](http://www.scriptscoop.net/t/0387723cc6b8/apache-spark-graphx-java-lang-arrayindexoutofboundsexception.html)

参考资料

- [SPARK-1405 Latent Dirichlet Allocation (LDA) using EM](https://github.com/apache/spark/pull/4047)
- [Spark Latent Dirichlet Allocation (LDA) Design Doc](https://docs.google.com/document/d/1kSsDqTeZMEB94Bs4GTd0mvdAmduvZSSkpoSfn-seAzo)