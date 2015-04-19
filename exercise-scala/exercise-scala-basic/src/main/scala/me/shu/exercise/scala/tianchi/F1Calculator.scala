package me.shu.exercise.scala.tianchi

import scala.io.Source
import scala.collection.mutable.Set

object F1Calculator {

  def main(args: Array[String]): Unit = {

    val candidates = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\item.csv").getLines drop 1 map { line =>
      line.split(",")(0)
    } toSet

    val baseline = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\buy-1.csv").getLines() drop (1) map { line =>
      val tokens = line.split(",")
      tokens(0) + "," + tokens(1)
    } filter (action => candidates.contains(action.split(",")(1))) toSet

    var lineIndex = 1

    var joinSetSize = 0
    var unionSetSize = 0

    var bestF1 = 0.0
    var info = ""

    Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\my.csv").getLines().drop(1) foreach { line =>

      val tokens = line.split(",")
      val record = tokens(0) + "," + tokens(1)

      lineIndex += 1

      if (baseline.contains(record)) {
        joinSetSize += 1
      }
      unionSetSize += 1

      val precision = 1.0 * joinSetSize / unionSetSize
      val recall = 1.0 * joinSetSize / baseline.size
      val f1 = 2.0 * precision * recall / (precision + recall)
      val currentInfo = joinSetSize + "-" + lineIndex + ": precision-" + precision + ", recall-" + recall + ", f1-" + f1

      if (f1 > bestF1) {
        bestF1 = f1
        info = currentInfo
      }
      println(currentInfo)
    }
    println("total buy-action count: " + baseline.size)
    println(info)
  }

}
