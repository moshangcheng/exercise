package me.shu.exercise.scala.recommender

import java.io.{File, PrintWriter}

import scala.collection.mutable
import scala.io.Source

/**
 * 使用SimilartiyCalculator计算出物品的相似度文件，预测用户对未购买商品的评分，将结果保存出在文件中
 */
object Predictor {

  def main(args: Array[String]) = {

    // 读取相似度
    val similarities = Source.fromFile("data/ml-100k/u1.similarity").getLines().map { line =>
      val tokens = line.split(",")
      (tokens(0).toInt, tokens.drop(0).map(_.toDouble))
    }

    // 读取训练数据集
    val ratings = Source.fromFile("data/ml-100k/u1.base").getLines().map { line =>
      val tokens = line.split("\t").take(3)
      (tokens(0).toInt, tokens(1).toInt, tokens(2).toDouble)
    }.toArray


    val userCount = ratings.maxBy(_._1)._1
    val itemCount = ratings.maxBy(_._2)._2

    val userFeatures = ratings.groupBy(_._1).map { case (userID, itemRatings) =>
      val ratingMap = new mutable.HashMap[Int, Double]()
      itemRatings.foreach { case (userID, itemID, rating) =>
        ratingMap += itemID -> rating
      }
      (userID, ratingMap)
    }

    // 根据物品相似度和用户已评分物品，计算未评分物品的评分


    // 写入预测评分，目前都是5分，注意已购买物品的预测评分无需写入
    val out = new PrintWriter(new File("data/ml-100k/u1.prediction"))
    (1 to userCount) foreach { i =>
      out.print(i + " ")
      val predictions = (1 to itemCount).filter { itemID =>
        !userFeatures(i).contains(itemID)
      }
      out.println(predictions.map(_ + ":" + 5).mkString(","))
    }
    out.close()

  }

}
