package me.shu.exercise.spark.basic

import me.shu.exercise.spark.util.Utils

import scala.math.random

import org.apache.spark._

/**
 * @author moshangcheng
 */
object PI {
  def main(args: Array[String]) {

    Utils.initializeHadoop()

    val sc = new SparkContext(new SparkConf().setAppName("Spark Pi").setMaster("local[*]"))

    val slices = if (args.length > 0) args(0).toInt else 2
    val n = math.min(100000L * slices, Int.MaxValue).toInt // avoid overflow
    val count = sc.parallelize(1 until n, slices).map { i =>
        val x = random * 2 - 1
        val y = random * 2 - 1
        if (x * x + y * y < 1) 1 else 0
      }.reduce(_ + _)
    println("Pi is roughly " + 4.0 * count / n)

    sc.stop()
  }
}