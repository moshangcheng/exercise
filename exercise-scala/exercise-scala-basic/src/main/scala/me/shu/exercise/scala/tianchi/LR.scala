package me.shu.exercise.scala.tianchi

import java.io.{File, PrintWriter}

import scala.collection.mutable.{Map, MutableList}
import scala.io.Source

object LR {

  def main(args: Array[String]): Unit = {

    var userDict = Map[Int, String]()
    var userReverseDict = Map[String, Int]()
    var itemDict = Map[Int, String]()
    var itemReverseDict = Map[String, Int]()

    var userActions = Map[Int, Map[Int, String]]()
    var prevTime = ""

    var positiveExampleCount = 0
    var negativeExampleCount = 0
    var removedActionCount = 0
    var ignoredActionCount = 0

    val lines = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\action-30.csv").getLines drop 1 // take 10 * 1000
    lines flatMap { line =>

      var localLRExamples = MutableList[(Int, String)]()

      val tokens = line.split(",")

      if (line.compare("LAST-LINE") == 0 || (!prevTime.isEmpty && tokens(5).compare(prevTime) != 0)) {
        userActions foreach { user =>
          user._2 foreach { action =>
            if (action._2.split(",")(2).toInt == 1) {
              // TODO add negative example
              localLRExamples += ((-1, action._2))
              user._2 -= action._1
            }
          }
        }
      }

      if (line.compare("LAST-LINE") == 0) {
        println("parse last line")
      } else {
        prevTime = tokens(5)
        if (!userReverseDict.contains(tokens(0))) {
          userReverseDict += tokens(0) -> userReverseDict.size
          userDict += userDict.size -> tokens(0)
        }
        if (!itemReverseDict.contains(tokens(1))) {
          itemReverseDict += tokens(1) -> itemReverseDict.size
          itemDict += itemDict.size -> tokens(1)
        }

        val currentActionType = tokens(2).toInt

        val userIndex = userReverseDict(tokens(0))
        if (!userActions.contains(userIndex)) {
          userActions += userIndex -> Map[Int, String]()
        }

        val itemIndex = itemReverseDict(tokens(1))
        if (!userActions(userIndex).contains(itemIndex)) {
          if (currentActionType == 4) {
            // TODO add positive example
            localLRExamples += ((1, line))
          } else {
            userActions(userIndex) += itemIndex -> line
          }
        }
        else {
          val prevAction = userActions(userIndex)(itemIndex).split(",")
          if (currentActionType == 4) {
            // TODO add positive example
            localLRExamples += ((1, line))
            userActions(userIndex) -= itemIndex
            removedActionCount += 1
          } else if (currentActionType >= prevAction(2).toInt) {
            userActions(userIndex)(itemIndex) = line
            // some click actions occurred at different locations are overrided
            removedActionCount += 1
          } else {
            ignoredActionCount += 1
          }
        }
      }
      localLRExamples
    } foreach { example =>
      if (example._1 == 1) {
        positiveExampleCount += 1
      } else {
        negativeExampleCount += 1
      }
    }

    println("remained action count: " + userActions.foldLeft(0)((sum, user) => sum + user._2.size))
    println("ignored action count: " + ignoredActionCount)
    println("overrided action count: " + removedActionCount)
    println("positive action count: " + positiveExampleCount)
    println("negative action count: " + negativeExampleCount)

    val output = new PrintWriter(new File("C:\\Users\\moshangcheng\\Desktop\\my.csv"))

    output.println("user_id,item_id,score")
    userActions foreach { user =>
      user._2 foreach { action =>
        output.println(userDict(user._1) + "," + itemDict(action._1))
      }
    }
    output.close()
  }

}
