package me.shu.exercise.scala.basic

/**
 * @author moshangcheng
 */
object TimerAnonymous {

  def oncePerSecond(callback: () => Unit) {
    while (true) {
      callback()
      Thread sleep 1000
    }
  }

  def main(args: Array[String]) {
    oncePerSecond { () => println("time files like an arrow...") }
  }
}