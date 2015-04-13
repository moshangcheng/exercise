package me.shu.exercise.scala.tianchi

import scala.io.Source

object F1Calculator {

  def main(args: Array[String]): Unit = {

    val baseline = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\buy-1.csv").getLines() drop (1) map { line =>
      val tokens = line.split(",")
      tokens(0) + "," + tokens(1)
    } toSet
    val result = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\my.csv").getLines().drop(1) map { line =>
      val tokens = line.split(",")
      tokens(0) + "," + tokens(1)
    } toSet

    val intersectSet = result.intersect(baseline)
    val unionSet = result.union(baseline)

    val precision = intersectSet.size / result.size
    val recall = intersectSet.size / unionSet.size
    val f1 = 2 * precision * recall / (precision + recall)

    println("precision: " + precision)
    println("recall: " + recall)
    println("f1: " + f1)
  }

}
