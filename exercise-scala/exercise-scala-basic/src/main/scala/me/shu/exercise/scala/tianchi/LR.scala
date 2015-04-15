package me.shu.exercise.scala.tianchi

import scala.collection.mutable.Map
import scala.collection.mutable.MutableList
import scala.io.Source

object LR {

  def main(args: Array[String]): Unit = {

    var userDict = Map[Int, String]()
    var userReverseDict = Map[String, Int]()
    var itemDict = Map[Int, String]()
    var itemReverseDict = Map[String, Int]()

    // build user dictionary and item dictionary
    Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\user-29.csv").getLines drop 1 foreach { x =>
      val tokens = x.split(",")
      if (!userReverseDict.contains(tokens(0))) {
        userReverseDict += tokens(0) -> userReverseDict.size
        userDict += userDict.size -> tokens(0)
      }
      if (!itemReverseDict.contains(tokens(1))) {
        itemReverseDict += tokens(1) -> itemReverseDict.size
        itemDict += itemDict.size -> tokens(1)
      }
    }

    var positiveExamples = MutableList[String]()
    var negativeExamples = MutableList[String]()

    // actions of different user are not related, so let's divide user to different groups
    // and analyze actions of users in each small group to reduce memory consumption in each iteration
    val PARTITION_SIZE = 1000
    (0 until userDict.size / PARTITION_SIZE) foreach { group =>

      val minUserIndex = group * PARTITION_SIZE
      val maxUserIndex = (group + 1) * PARTITION_SIZE

      var userActions = Map[Int, Map[Int, String]]()

      Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\user-29.csv").getLines drop 1 take 1000 * 1000 foreach { line =>
        val tokens = line.split(",")
        val userIndex = userReverseDict(tokens(0))
        if (userIndex >= minUserIndex || userIndex < maxUserIndex) {
          if (!userActions.contains(userIndex)) {
            userActions += userIndex -> Map[Int, String]()
          }
          val itemIndex = itemReverseDict(tokens(1))
          if (!userActions(userIndex).contains(itemIndex)) {
            userActions(userIndex) += itemIndex -> line
          }
          else {
            val prevAction = userActions(userIndex)(itemIndex).split(",")
            val currentActionType = tokens(2).toInt
            if (currentActionType == 4) {
              // TODO add positive example
              positiveExamples += line
            } else {
              // no action after viewing this item
              if (prevAction(2) == 1 && prevAction(5).compare(tokens(5)) != 0) {
                // TODO add negative example
                negativeExamples += userActions(userIndex)(itemIndex)
              }
              // override previouse action if current action is more important
              if (currentActionType >= prevAction(2).toInt) {
                userActions(userIndex)(itemIndex) = line
              }
            }
          }
        }
      }

      userActions foreach { user =>
        user._2 foreach { action =>
          if (action._2.split(",")(2).toInt == 1) {
            // TODO add negative example
            negativeExamples += action._2
          }
        }
      }

      userActions clear
    }

    println(positiveExamples.count(x => true))
    println(negativeExamples.count(x => true))
  }

}
