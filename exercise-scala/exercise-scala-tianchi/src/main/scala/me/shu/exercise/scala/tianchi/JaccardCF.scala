package me.shu.exercise.scala.tianchi

import java.io.{File, PrintWriter}

import scala.io.Source
import scala.collection.mutable.Map
import scala.collection.mutable.Set

/**
 * Tianchi competition solution using Jaccard based CF
 */
object JaccardCF {

  def main(args: Array[String]) {

    val userData = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\action-29.csv").getLines()
    val output = new PrintWriter(new File("C:\\Users\\moshangcheng\\Desktop\\my.csv"))

    userData.drop(1)

    var userDict = Map[Int, String]()
    var userReverseDict = Map[String, Int]()
    var itemDict = Map[Int, String]()
    var itemReverseDict = Map[String, Int]()

    var userFeatures = Map[Int, Set[Int]]()

    userData foreach { x =>
      val tokens = x.split(",")
      if (tokens(2).toInt == 4) {
        if (!userReverseDict.contains(tokens(0))) {
          userFeatures += userDict.size -> Set[Int]()
          userReverseDict += tokens(0) -> userReverseDict.size
          userDict += userDict.size -> tokens(0)
        }
        if (!itemReverseDict.contains(tokens(1))) {
          itemReverseDict += tokens(1) -> itemReverseDict.size
          itemDict += itemDict.size -> tokens(1)
        }
        userFeatures(userReverseDict(tokens(0))) += itemReverseDict(tokens(1))
      }
    }

    var userSimilarity = Map[Int, Map[Int, Double]]()

    (0 until userDict.size) foreach { u =>
      userSimilarity += u -> Map[Int, Double]()
      (0 until userDict.size) foreach { v =>
        if (u < v) {
          val intersectSize = userFeatures(u).intersect(userFeatures(v)).size
          if (intersectSize > 0) {
            userSimilarity(u)(v) =
              1.0 * intersectSize / userFeatures(u).union(userFeatures(v)).size
          }
        } else if (u > v && userSimilarity(v).contains(u)) {
          userSimilarity(u)(v) = userSimilarity(v)(u)
        }
      }
    }

    //    for (u <- 0 until userSimilarity.length) {
    //      print(u)
    //      for (v <- 0 until userSimilarity(u).length) {
    //        if (userSimilarity(u)(v) > 0) {
    //          print(" " + v + ":" + userSimilarity(u)(v))
    //        }
    //      }
    //      println()
    //    }

    //    println(userSimilarity.foldLeft(0)((sum, u) => sum + u._2.size))

    val p = (0 until userDict.size) flatMap { u =>
      val totalSimilarity = userSimilarity(u).foldLeft(0.0)(_ + _._2)
      val sum = Map[Int, Double]()
      userSimilarity(u) foreach { v =>
        userFeatures(v._1) foreach { i =>
          if (sum.contains(i)) {
            sum(i) = sum(i) + v._2
          } else {
            sum(i) = v._2
          }
        }
      }
      sum filter { i => !userFeatures(u).contains(i._1)} map { i =>
        (userDict(u), itemDict(i._1), i._2 / totalSimilarity)
      }
    } sortBy (-_._3)

    output.println("user_id,item_id,score")
    p foreach { x => output.println(x._1 + "," + x._2 + "," + x._3)}
    output.close()
  }
}