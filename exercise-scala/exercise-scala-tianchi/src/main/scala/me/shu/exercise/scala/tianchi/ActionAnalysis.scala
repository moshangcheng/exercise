package me.shu.exercise.scala.tianchi

import scala.io.Source

object ActionAnalysis {

  def main(args: Array[String]) {

    val userData = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\action-29.csv").getLines()

    userData.drop(1)

    userData.flatMap { x =>
      val tokens = x.split(",")
      if (tokens(2).toInt == 4)
        Array((tokens(0), tokens(1), tokens(5).split(" ")(0)))
      else
        Array[(String, String, String)]()
    }.toArray.groupBy(_._3).toArray.sortBy(_._1).foreach { x =>
      println(x._1, x._2.length)
    }
  }
}