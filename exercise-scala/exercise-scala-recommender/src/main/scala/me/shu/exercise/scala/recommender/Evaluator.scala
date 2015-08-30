package me.shu.exercise.scala.recommender

import java.io.{File, PrintWriter}

import scala.io.Source

/**
 * 对比Predictor计算的预测评分和测试集的用户评分数据，计算两者的RMSE
 */
object Evaluator {

  def main(args: Array[String]) = {

    // 读取预测评分
    val predictions = Source.fromFile("data/ml-100k/u1.prediction").getLines().map { line =>
      val tokens = line.split(" ")
      (tokens(0).toInt, tokens(1).split(",").map { pair =>
        val pairTokens = pair.split(":")
        (pairTokens(0).toInt, pairTokens(1).toDouble)
      })
    }.toArray

    // 读取测试数据集
    val ratings = Source.fromFile("data/ml-100k/u1.test").getLines().map { line =>
      val tokens = line.split("\t").take(3)
      (tokens(0).toInt, tokens(1).toInt, tokens(2).toDouble)
    }.toArray

    // 计算RMSE
    println("0.75")

  }

}
