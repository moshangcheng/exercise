package me.shu.exercise.scala.tianchi

import java.io.{File, PrintWriter}

import scala.collection.mutable.{Map, MutableList}
import scala.io.Source
import scala.util.Random

object LR {

  private val exampleRatio = 0.05
  private val relFeatureRatio = 0.005

  private val alpha = 5
  private val beta = 1.0
  private val lambda1 = 0.5
  private val lambda2 = 0.5

  private val userDict = Map[Int, String]()
  private val userReverseDict = Map[String, Int]()
  private val itemDict = Map[Int, String]()
  private val itemReverseDict = Map[String, Int]()

  // userID, itemID, actionType, itemCat, interval
  private val actions = Map[Int, Map[Int, (Int, Int, Int)]]()
  private val valuableActions = Map[Int, MutableList[(Int, Int, Int, Int)]]()
  // not used, click action count, collect action count, add-cart action count, buy action count
  // the sum of collect count of each buy action, the sum of add-cart count of each buy action
  private val userStat = Map[Int, Array[Int]]()

  private val featureDict = Map[String, Int]()
  private val featureReverseDict = Map[Int, String]()
  private val weights = Map[Int, Double]()

  private val ftrlZ = Map[Int, Double]()
  private val ftrlN = Map[Int, Double]()


  def main(args: Array[String]): Unit = {

    val baseFeature = (userID: Int, itemID: Int, itemCat: Int) => {
      MutableList[String]("item.id:" + itemID, "item.cat:" + itemCat, "user.id:" + userID, "user.id*item.cat:" + userID + "*" + itemCat)
    }

    val relationFeature = (action: (Int, (Int, Int, Int)), valuableAction: (Int, Int, Int, Int)) => {
      MutableList[String](
        ("rel@item.id*item.id:" + {
          if (action._1 >= valuableAction._1) {
            valuableAction._1 + "*" + action._1
          } else {
            action._1 + "*" + valuableAction._1
          }
        })
//        , ("rel@" + valuableAction._2 + ":item.cat*item.cat:" + {
//          if (action._2._2 >= valuableAction._3) {
//            valuableAction._3 + "*" + action._2._2
//          } else {
//            action._2._2 + "*" + valuableAction._3
//          }
//        })
      )
    }

    val filterFeature = (feature: String) => {
      val filterRatio = if (feature.startsWith("rel@")) {
        relFeatureRatio
      } else {
        1.0
      }
      if (featureDict.contains(feature)) {
        true
      } else if (Random.nextDouble() < filterRatio) {
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
        "t:" + interval
        , "t-item.id:" + interval + "-" + prevAction._1
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
                  val exampleFeature = baseFeature(user._1, action._1, action._2._2)
                  exampleFeature ++= valuableActions(user._1).flatMap(relationFeature(action, _))
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
          userStat += userReverseDict.size -> Array.fill(7)(0)
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
              val exampleFeature = baseFeature(userID, action._1, action._2._2)
              exampleFeature ++= valuableActions(userID).view.flatMap(relationFeature(action, _))
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
              val exampleFeature = baseFeature(userID, action._1, action._2._2)
              exampleFeature ++= valuableActions(userID).view.flatMap(relationFeature(action, _))
              if (prevAction._1 > 1) {
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

        if (action._2._1 == 4) {
          valuableActions(userID) += ((action._1, action._2._1, action._2._2, action._2._3))
          userStat(userID)(5) += userStat(userID)(2)
          userStat(userID)(6) += userStat(userID)(3)
        }
        userStat(userID)(action._2._1) += 1
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
        var gti = gt
        if (featureReverseDict(dim).startsWith("rel@")) {
          gti = gti / relFeatureRatio
        }
        val ftrlNi = ftrlN.getOrElse(dim, 0.0)
        val ftrlZi = ftrlZ.getOrElse(dim, 0.0)
        val sigma = 1.0 / alpha * (math.sqrt(ftrlNi + gti * gti) - math.sqrt(ftrlNi))
        ftrlZ(dim) = ftrlZi + gti - sigma * weights.getOrElse(dim, 0.0)
        ftrlN(dim) = ftrlNi + gti * gti
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


    val featureOutput = new PrintWriter(new File("C:\\Users\\moshangcheng\\Desktop\\feature.csv"))
    //val weightsString = weights.toArray.sortBy(-_._2).view.map { w => featureReverseDict(w._1) + "," + w._2}.foreach(featureOutput.println)
    featureOutput.close()

    val candidates = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\item.csv").getLines drop 1 map { line =>
      line.split(",")(0)
    } filter (itemReverseDict.contains(_)) map (itemReverseDict(_)) toSet

    val hotItems = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\hot-item.csv").getLines map { line =>
      var tokens = line.split(",")
      (tokens(0), tokens(1).toInt)
    } filter { hotItem =>
      itemReverseDict.contains(hotItem._1) && candidates.contains(itemReverseDict(hotItem._1))
    } map (hotItem => (itemReverseDict(hotItem._1), hotItem._2)) toList

    val result = actions flatMap { user =>
      val userID = user._1
      val collectedItemCount = user._2.count(_._2._1 == 2)
      val inCartItemCount = user._2.count(_._2._1 == 3)
      user._2.view.filter(action => candidates.contains(action._1)) map { action =>
        (user._1, action._1, {
          val candidateFeature = baseFeature(userID, action._1, action._2._2) ++ valuableActions(userID).flatMap { otherAction =>
            relationFeature(action, otherAction)
          } ++ intervalFeature(userID, 30, action)
          val baseWeight = candidateFeature.foldLeft(0.0) { (acc, dim) =>
            if (featureDict.contains(dim)) {
              acc + weights.getOrElse(featureDict(dim), 0.0)
            } else {
              acc
            }
          }
          if (action._2._1 == 2) {
            baseWeight * userStat(userID)(2) / collectedItemCount // collectedItemCount
          } else if (action._2._1 == 3) {
            baseWeight * userStat(userID)(3) / inCartItemCount // inCartItemCount
          } else {
            baseWeight
          }
        })
      }
    } toList

    val finalResult = result ++ hotItems.flatMap { hotItem =>
      userDict map { user => (user._1, hotItem._1, {
        val candidateFeature = baseFeature(user._1, hotItem._1, hotItem._2) ++ valuableActions(user._1).flatMap { otherAction =>
          relationFeature((hotItem._1, (-1, hotItem._2, -1)), otherAction)
        }
        candidateFeature.foldLeft(0.0) { (acc, dim) =>
          if (featureDict.contains(dim)) {
            acc + weights.getOrElse(featureDict(dim), 0.0)
          } else {
            acc
          }
        }
      })
      } toList
    }

    val output = new PrintWriter(new File("C:\\Users\\moshangcheng\\Desktop\\my.csv"))

    output.println("user_id,item_id")
    finalResult.sortBy(-_._3).take(2000).foreach { x => output.println(userDict(x._1) + "," + itemDict(x._2))}
    output.close()
  }

}
