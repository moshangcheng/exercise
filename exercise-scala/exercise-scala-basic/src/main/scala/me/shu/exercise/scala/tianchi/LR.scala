package me.shu.exercise.scala.tianchi

import java.io.{File, PrintWriter}

import scala.collection.mutable
import scala.collection.mutable.{Map, MutableList}
import scala.io.Source
import scala.util.Random

object LR {

  private val exampleRatio = 0.1
  private val featureRatio = 0.001

  private val alpha = 2.0
  private val beta = 1.0
  private val lambda1 = 1.0e-5
  private val lambda2 = 0.2

  private val userDict = Map[Int, String]()
  private val userReverseDict = Map[String, Int]()
  private val itemDict = Map[Int, String]()
  private val itemReverseDict = Map[String, Int]()

  private val actions = Map[Int, Map[Int, (Int, Int, Int)]]()
  private val valuableActions = Map[Int, MutableList[(Int, Int, Int, Int)]]()

  private val featureDict = Map[String, Int]()
  private val featureReverseDict = Map[Int, String]()
  private val weights = Map[Int, Double]()

  private val ftrlZ = Map[Int, Double]()
  private val ftrlN = Map[Int, Double]()


  def main(args: Array[String]): Unit = {

    val baseFeature = (userID: Int, action: (Int, (Int, Int, Int))) => {
      MutableList[String]("item.id:" + action._1, "item.cat:" + action._2._2, "user.id:" + userID)
    }

    val relationFeature = (action: (Int, (Int, Int, Int)), valueableAction: (Int, Int, Int, Int)) => {
      MutableList[String](
        ("cf-" + valueableAction._2 + ":item.id*item.id:" + {
          if (action._1 >= valueableAction._1) {
            valueableAction._1 + "*" + action._1
          } else {
            action._1 + "*" + valueableAction._1
          }
        })
        , ("cf-" + valueableAction._2 + ":item.cat*item.cat:" + {
          if (action._2._2 >= valueableAction._3) {
            valueableAction._3 + "*" + action._2._2
          } else {
            action._2._2 + "*" + valueableAction._3
          }
        })
      )
    }

    val filterFeature = (feature: String) => {
      if (featureDict.contains(feature)) {
        true
      } else if (Random.nextDouble() < featureRatio) {
        featureDict += feature -> featureDict.size
        featureReverseDict += featureReverseDict.size -> feature
        true
      } else {
        false
      }
    }

    val intervalFeature = (userID: Int, currentInterval: Int, prevAction: (Int, (Int, Int, Int))) => {
      val interval = currentInterval - prevAction._2._3
      MutableList[String](
        "t-item.id:" + interval + "-" + prevAction._1
        , "t-action.type:" + interval + "-" + prevAction._2._1
        , "t-item.cat:" + interval + "-" + prevAction._2._2
        , "t-action.type*item.cat:" + interval + "-" + prevAction._2._1 + "*" + prevAction._2._2
        , "t-user.id:" + interval + "-" + userID
        , "t-user.id*action.type:" + interval + "-" + userID + "*" + prevAction._2._1
        , "t-user.id*item.cat:" + interval + "-" + userID + "*" + prevAction._2._2
      )
    }

    val getInterval = (date: String) => {
      val baseMonth = 11
      val baseDay = 18
      val tokens = date.split(" ")(0).split("-")
      (tokens(1).toInt - baseMonth) * 30 + (tokens(2).toInt - baseDay)
    }

    var prevTime = ""

    var positiveExampleCount = 0
    var negativeExampleCount = 0
    var removedActionCount = 0
    var ignoredActionCount = 0

    val lines = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\action-29.csv").getLines drop 1
    lines flatMap { line =>

      val localLRExamples = MutableList[(Int, MutableList[Int])]()

      val tokens = line.split(",")

      // at a new time now, clear clicking actions in the previous hour
      if (line.compare("LAST-LINE") == 0 || (!prevTime.isEmpty && tokens(5).compare(prevTime) != 0)) {
        actions foreach { user =>
          user._2 foreach { action =>
            if (action._2._1 == 1) {
              // add negative example
              if (Random.nextDouble() < exampleRatio) {
                localLRExamples += ((0, {
                  // extract feature
                  val exampleFeature = baseFeature(user._1, action) ++ valuableActions(user._1).flatMap(relationFeature(action, _))
                  exampleFeature.filter(filterFeature(_)).map(featureDict)
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
          valuableActions += userReverseDict.size -> MutableList[(Int, Int, Int, Int)]()
          userReverseDict += tokens(0) -> userReverseDict.size
          userDict += userDict.size -> tokens(0)
        }
        if (!itemReverseDict.contains(tokens(1))) {
          itemReverseDict += tokens(1) -> itemReverseDict.size
          itemDict += itemDict.size -> tokens(1)
        }

        val userID = userReverseDict(tokens(0))
        if (!actions.contains(userID)) {
          actions += userID -> Map[Int, (Int, Int, Int)]()
        }

        val action = (itemReverseDict(tokens(1)), (tokens(2).toInt, tokens(4).toInt, getInterval(tokens(5))))

        if (!actions(userID).contains(action._1)) {
          if (action._2._1 == 4) {
            // add positive example
            localLRExamples += ((1, {
              // extract feature
              val exampleFeature = baseFeature(userID, action) ++ valuableActions(userID).flatMap(relationFeature(action, _))
              exampleFeature.filter(filterFeature(_)).map(featureDict)
            }))
          } else {
            actions(userID) += action
          }
        }
        else {
          val prevAction = actions(userID)(action._1)
          if (action._2._1 == 4) {
            // add positive example
            localLRExamples += ((1, {
              // extract feature
              val exampleFeature = baseFeature(userID, action) ++ valuableActions(userID).flatMap(relationFeature(action, _))
              if (prevAction._1 > 1) {
                // TODO add action interval as feature
                exampleFeature ++= intervalFeature(userID, action._2._3, (action._1, prevAction))
              }
              exampleFeature.filter(filterFeature(_)).map(featureDict)
            }))
            actions(userID) -= action._1
            removedActionCount += 1
          } else if (action._2._1 >= prevAction._1) {
            actions(userID) += action
            // some click actions occurred at different locations are overrided
            removedActionCount += 1
          } else {
            // TODO add negative example
            ignoredActionCount += 1
          }
        }
        if (action._2._1 != 1) {
          valuableActions(userID) += ((action._1, action._2._1, action._2._2, action._2._3))
        }

      }
      localLRExamples
    } foreach { example =>

      val pt = 1.0 / (1.0 + math.exp(example._2.view.map(weights.getOrElse(_, 0.0)).sum))
      val gt = (pt - example._1) * {
        if (example._1 > 0) {
          1.0
        } else {
          1.0 / exampleRatio
        }
      }

      example._2 foreach { dim =>
        val ftrlNi = ftrlN.getOrElse(dim, 0.0)
        val ftrlZi = ftrlZ.getOrElse(dim, 0.0)
        val sigma = 1.0 / alpha * (math.sqrt(ftrlNi + gt * gt) - math.sqrt(ftrlNi))
        ftrlZ(dim) = ftrlZi + gt - sigma * weights.getOrElse(dim, 0.0)
        ftrlN(dim) = ftrlNi + gt * gt
        if (math.abs(ftrlZ(dim)) > lambda1) {
          weights(dim) = -1.0 / (lambda2 + (beta + math.sqrt(ftrlN(dim)) / alpha)) * (ftrlZ(dim) - math.signum(ftrlZ(dim)) * lambda1)
        } else {
          weights -= dim
        }
      }

      if (example._1 == 1) {
        positiveExampleCount += 1
      } else {
        negativeExampleCount += 1
      }
    }

    println("remained action count: " + actions.foldLeft(0)((sum, user) => sum + user._2.size))
    println("ignored action count: " + ignoredActionCount)
    println("overrided action count: " + removedActionCount)

    println("positive action count: " + positiveExampleCount
      + ", negative action count: " + negativeExampleCount)

    println("total weight count:" + weights.size
      + ", positive weight count: " + weights.count(w => w._2 > 0.0)
      + ", negative weight count: " + weights.count(w => w._2 < 0.0))

    val candidates = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\item.csv").getLines drop 1 map { line =>
      line.split(",")(0)
    } filter (itemReverseDict.contains(_)) map (itemReverseDict(_)) toSet

    val result = actions flatMap { user =>
      val userID = user._1
      user._2.view.filter(action => candidates.contains(action._1)) map { action =>
        (user._1, action._1, {
          val candidateFeature = baseFeature(userID, action) ++ actions(userID).flatMap { otherAction =>
            relationFeature(action, (otherAction._1, otherAction._2._1, otherAction._2._2, otherAction._2._3))
          } ++ intervalFeature(userID, 30, action)
          candidateFeature.foldLeft(0.0) { (acc, dim) =>
            if (featureDict.contains(dim)) {
              acc + weights.getOrElse(featureDict(dim), 0.0)
            } else {
              acc
            }
          }
        })
      }
    } toList

    val output = new PrintWriter(new File("C:\\Users\\moshangcheng\\Desktop\\my-all.csv"))

    output.println("user_id,item_id,score")
    result.sortBy(-_._3).foreach { x => output.println(userDict(x._1) + "," + itemDict(x._2) + "," + x._3)}
    output.close()
  }

}
