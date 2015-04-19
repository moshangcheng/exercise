package me.shu.exercise.scala.tianchi

import scala.io.Source

object F1Calculator {

  def main(args: Array[String]): Unit = {

    val candidates = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\item.csv").getLines drop 1 map { line =>
      line.split(",")(0)
    } toSet

    val baseline = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\buy-1.csv").getLines() drop (1) map { line =>
      val tokens = line.split(",")
      tokens(0) + "," + tokens(1)
    } filter (action => candidates.contains(action.split(",")(1))) toSet
    val result = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\my.csv").getLines().drop(1) map { line =>
      val tokens = line.split(",")
      tokens(0) + "," + tokens(1)
    } toSet

    val intersectSet = result.intersect(baseline)
    val unionSet = result.union(baseline)

    val precision = 1.0 * intersectSet.size / result.size
    val recall = 1.0 * intersectSet.size / baseline.size
    val f1 = 2.0 * precision * recall / (precision + recall)

    print("intersection set size:" + intersectSet.size)
    println(", union set size:" + unionSet.size)
    print("precision: " + precision)
    print(", recall: " + recall)
    println(", f1: " + f1)
  }

}
