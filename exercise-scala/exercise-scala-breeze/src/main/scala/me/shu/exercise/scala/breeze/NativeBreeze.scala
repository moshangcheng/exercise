package me.shu.exercise.scala.breeze

import breeze.linalg.{DenseVector, DenseMatrix}


object NativeBreeze {

  def main(args: Array[String]): Unit = {
    val m = DenseMatrix.rand(10, 10)
    val a = DenseVector.rand(10)
    m * a
  }

}
