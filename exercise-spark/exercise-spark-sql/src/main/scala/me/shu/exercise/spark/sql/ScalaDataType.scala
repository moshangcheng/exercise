package me.shu.exercise.spark.sql

import me.shu.exercise.spark.util.Utils
import org.apache.spark.sql.types._
import org.apache.spark.{SparkConf, SparkContext}


object ScalaDataType {

  def main(args: Array[String]): Unit = {

    Utils.initializeHadoop()

    val sc = new SparkContext("local", "local-hive-test", new SparkConf())
    val hc = Utils.createHiveContext(sc)

    {
      val a = hc.sql(
        """
          |select array(named_struct("a", 1, "b", 1), named_struct("a", 2, "b", 2)) as v
        """.stripMargin
      )

      val b = a.map { row =>
        row
      }

      val schema = StructType(
        Array(
          StructField(
            "v",
            ArrayType(
              StructType(Array(
                StructField("a", IntegerType, true),
                StructField("b", IntegerType, true)
              ))
            ), true
          )
        )
      )

      hc.createDataFrame(b, schema).registerTempTable("a")
      hc.sql("select v.a, v.b from a").show()
    }
  }
}
