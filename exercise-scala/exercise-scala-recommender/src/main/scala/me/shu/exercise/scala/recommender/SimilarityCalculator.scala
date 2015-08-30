package me.shu.exercise.scala.recommender

import java.io.{PrintWriter, FileWriter, File}

import scala.io.Source


/**
 * 根据Pearson相似度公式，计算训练数据集中物品的相似度，将结果保存出在文件中
 */
object SimilarityCalculator {

  def main(args: Array[String]) = {

    // 读取训练数据集
    val ratings = Source.fromFile("data/ml-100k/u1.base").getLines().map { line =>
      val tokens = line.split("\t").take(3)
      (tokens(0).toInt, tokens(1).toInt, tokens(2).toDouble)
    }.toArray

    val userCount = ratings.maxBy(_._1)._1
    val itemCount = ratings.maxBy(_._2)._2

    // 计算相似度

    // 写入相似度，现在都是写入1
    val out = new PrintWriter(new File("data/ml-100k/u1.similarity"))
    (1 to itemCount) foreach { i =>
      out.print(i)
      out.print(" 1" * itemCount)
      out.println()
    }
    out.close()

  }

}
