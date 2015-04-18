package me.shu.exercise.scala.tianchi

import java.io.{File, PrintWriter}

import scala.collection.mutable.{Map, MutableList}
import scala.io.Source
import scala.util.Random

object LR {

  def main(args: Array[String]): Unit = {

    var exampleRation = 0.1
    var featureRatio = 0.1

    val baseFeature = (user: Int, action: (Int, (Int, Int))) => {
      MutableList[String]("item.id:" + action._1, "item.cat:" + action._2._2, "user.id:" + user)
    }

    val relationFeature = (action: (Int, (Int, Int)), valueableAction: (Int, Int, Int)) => {
      MutableList[String](
        (valueableAction._2 + ":item.id*item.id:" + {
          if (action._1 >= valueableAction._1) {
            valueableAction._1 + "*" + action._1
          } else {
            action._1 + "*" + valueableAction._1
          }
        })
        , (valueableAction._2 + ":item.cat*item.cat:" + {
          if (action._2._2 >= valueableAction._3) {
            valueableAction._3 + "*" + action._2._2
          } else {
            action._2._2 + "*" + valueableAction._3
          }
        })
      )
    }

    val filterFeature = (feature: String, features: Map[String, Double]) => {
      features.contains(feature) || (Random.nextDouble() < featureRatio && (features += feature -> 0.0) != null)
    }

    var userDict = Map[Int, String]()
    var userReverseDict = Map[String, Int]()
    var itemDict = Map[Int, String]()
    var itemReverseDict = Map[String, Int]()

    var actions = Map[Int, Map[Int, (Int, Int)]]()
    var features = Map[String, Double]()
    var valuableActions = Map[Int, MutableList[(Int, Int, Int)]]()

    var prevTime = ""

    var positiveExampleCount = 0
    var negativeExampleCount = 0
    var removedActionCount = 0
    var ignoredActionCount = 0

    val lines = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\action-29.csv").getLines drop 1 // take 10 * 1000
    lines flatMap { line =>

      var localLRExamples = MutableList[(Int, MutableList[String])]()

      val tokens = line.split(",")

      // at a new time now, clear clicking actions in the previous hour
      if (line.compare("LAST-LINE") == 0 || (!prevTime.isEmpty && tokens(5).compare(prevTime) != 0)) {
        actions foreach { user =>
          user._2 foreach { action =>
            if (action._2._1 == 1) {
              // add negative example
              if (Random.nextDouble() < exampleRation) {
                localLRExamples += ((-1, {
                  // extract feature
                  var exampleFeature = valuableActions(user._1) flatMap (relationFeature(action, _))
                  exampleFeature ++= baseFeature(user._1, action)
                  exampleFeature filter (filterFeature(_, features))
                }))
              }
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
          valuableActions += userReverseDict.size -> MutableList[(Int, Int, Int)]()
          userReverseDict += tokens(0) -> userReverseDict.size
          userDict += userDict.size -> tokens(0)
        }
        if (!itemReverseDict.contains(tokens(1))) {
          itemReverseDict += tokens(1) -> itemReverseDict.size
          itemDict += itemDict.size -> tokens(1)
        }

        val userIndex = userReverseDict(tokens(0))
        if (!actions.contains(userIndex)) {
          actions += userIndex -> Map[Int, (Int, Int)]()
        }

        val action = (itemReverseDict(tokens(1)), (tokens(2).toInt, tokens(4).toInt))
        if (action._2._1 != 1) {
          valuableActions(userIndex) += ((action._1, action._2._1, action._2._2))
        }

        if (!actions(userIndex).contains(action._1)) {
          if (action._2._1 == 4) {
            // add positive example
            localLRExamples += ((1, {
              // extract feature
              var exampleFeature = valuableActions(userIndex) flatMap (relationFeature(action, _))
              exampleFeature ++= baseFeature(userIndex, action)
              exampleFeature filter (filterFeature(_, features))
            }))
          } else {
            actions(userIndex) += action
          }
        }
        else {
          val prevAction = actions(userIndex)(action._1)
          if (action._2._1 == 4) {
            // add positive example
            localLRExamples += ((1, {
              // extract feature
              var exampleFeature = valuableActions(userIndex) flatMap (relationFeature(action, _))
              if (prevAction._1 > 1) {
                // TODO add action interval as feature
                //exampleFeature += ""
              }
              exampleFeature ++= baseFeature(userIndex, action)
              exampleFeature filter (filterFeature(_, features))
            }))
            actions(userIndex) -= action._1
            removedActionCount += 1
          } else if (action._2._1 >= prevAction._1) {
            actions(userIndex) += action
            // some click actions occurred at different locations are overrided
            removedActionCount += 1
          } else {
            // TODO add negative example
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

    println("remained action count: " + actions.foldLeft(0)((sum, user) => sum + user._2.size))
    println("ignored action count: " + ignoredActionCount)
    println("overrided action count: " + removedActionCount)
    println("positive action count: " + positiveExampleCount)
    println("negative action count: " + negativeExampleCount)
    println("feature count: " + features.size)

    val output = new PrintWriter(new File("C:\\Users\\moshangcheng\\Desktop\\my.csv"))

    output.println("user_id,item_id,score")
    actions foreach { user =>
      user._2 foreach { action =>
        output.println(userDict(user._1) + "," + itemDict(action._1))
      }
    }
    output.close()
  }

}
