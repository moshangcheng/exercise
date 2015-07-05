package me.shu.exercise.spark.hive

import me.shu.exercise.spark.util.Utils
import org.apache.spark.sql.hive.ClosableHiveContext
import org.apache.spark.{SparkConf, SparkContext}




/**
 * Created by moshangcheng on 2015/6/10.
 */
object LocalHive {


  def main(args: Array[String]): Unit = {

    Utils.initializeHadoop()

    val sc = new SparkContext("local", "local-hive-test", new SparkConf())
    val hc = Utils.createHiveContext(sc)

    hc.sql("create database if not exists local_test")
    hc.sql("use local_test")

    {
      hc.sql("create table if not exists normal_table (id int, lang string) row format delimited fields terminated by ' '")
      hc.sql(s"load data local inpath '${Utils.getFileInClasspath("normal_table.csv")}' overwrite into table normal_table")
      hc.sql("select * from normal_table").foreach(println)
    }

    {
      hc.sql("create table if not exists partitioned_table (id int, name string) partitioned by (age int) row format delimited fields terminated by ' '")
      hc.sql(s"load data local inpath '${Utils.getFileInClasspath("partitioned_table_part1.csv")}' overwrite into table partitioned_table partition (age = 18)")
      hc.sql(s"load data local inpath '${Utils.getFileInClasspath("partitioned_table_part2.csv")}' overwrite into table partitioned_table partition (age = 19)")
      hc.sql("select * from partitioned_table").foreach(println)
      hc.sql("show partitions partitioned_table").foreach(println)
    }

    {
      hc.udf.register("hasAV", (x: String) => x.contains("av"))
      hc.sql("select * from normal_table where hasAV(lang)").foreach(println)
    }


    new ClosableHiveContext(hc).close()
    sc.stop()

  }
}
