package me.shu.exercise.scala.tianchi

import java.io._
import scala.io.Source

object UserDataSplitter {

  def main(args: Array[String]) {

    val userData = Source.fromFile("C:\\Users\\moshangcheng\\Desktop\\uniq-sort-user.csv").getLines()
    val output29 = new PrintWriter(new File("C:\\Users\\moshangcheng\\Desktop\\user-29.csv"))
    val output1 = new PrintWriter(new File("C:\\Users\\moshangcheng\\Desktop\\buy-1.csv"))

    val header = userData.next()
    output29.println(header)
    output1.println(header)

    userData foreach { line =>
      val tokens = line.split(",")
      if (tokens(5).split(" ")(0).compareTo("2014-12-18") < 0) {
        output29.println(line)
      } else if (tokens(2).toInt == 4) {
        output1.println(line)
      }
    }

    output29.close()
    output1.close()
  }

}
