package me.shu.exercise.scala.tianchi

import java.io._
import scala.io.Source
import scala.collection.mutable.Map

object UserDataSplitter {

  def main(args: Array[String]) {

    val items = Map[String, (Int, Int)]()
    val userData = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\uniq-sort-action.csv").getLines()
    val output29 = new PrintWriter(new File("C:\\Users\\moshangcheng\\Desktop\\action-29.csv"))
    val output30 = new PrintWriter(new File("C:\\Users\\moshangcheng\\Desktop\\action-30.csv"))
    val output1 = new PrintWriter(new File("C:\\Users\\moshangcheng\\Desktop\\buy-1.csv"))
    val hotItems = new PrintWriter(new File("C:\\Users\\moshangcheng\\Desktop\\hot-item.csv"))


    val header = userData.next()
    output1.println(header)
    output29.println(header)
    output30.println(header)

    userData foreach { line =>
      output30.println(line)
      val tokens = line.split(",")
      if (tokens(5).split(" ")(0).compareTo("2014-12-18") < 0) {
        output29.println(line)
      } else if (tokens(2).toInt == 4) {
        output1.println(line)
      }

      if (tokens(2).toInt == 4) {
        items(tokens(1)) = (tokens(4).toInt, items.getOrElse(tokens(1), (0, 0))._2 + 1)
      }
    }

    output29.println("LAST-LINE")
    output30.println("LAST-LINE")

    items.filter(_._2._2 > 15).foreach(item => hotItems.println(item._1 + "," + item._2._1))

    hotItems.close()
    output30.close()
    output29.close()
    output1.close()
  }

}
