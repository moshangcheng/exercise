package me.shu.exercise.scala.basic

abstract class Tree
case class Multi(l: Tree, r: Tree) extends Tree
case class Sum(l: Tree, r: Tree) extends Tree
case class Var(n: String) extends Tree
case class Const(v: Int) extends Tree

/**
 * @moshangcheng
 */
object PatternMatching {

  type Environment = String => Int

  def eval(t: Tree, env: Environment): Int = t match {
    case Multi(l, r) => eval(l, env) * eval(r, env)
    case Sum(l, r) => eval(l, env) + eval(r, env)
    case Var(n)    => env(n)
    case Const(v)  => v
  }

  def derive(t: Tree, v: String): Tree = t match {
    case Multi(l, r)        => Sum(Multi(derive(l, v), r), Multi(l, derive(r, v)))
    case Sum(l, r)          => Sum(derive(l, v), derive(r, v))
    case Var(n) if (v == n) => Const(1)
    case _                  => Const(0)
  }

  def main(args: Array[String]) {
    val exp: Tree = Sum(Multi(Var("x"), Var("y")), Sum(Const(7), Var("y")))
    val env: Environment = { case "x" => 5 case "y" => 7 }
    println("Expression: " + exp)
    println("Evaluation with x=5, y=7: " + eval(exp, env))
    println("Derivative relative to x:\n " + derive(exp, "x"))
    println("Derivative relative to y:\n " + derive(exp, "y"))
    println("Derivative relative to x at x=5, y=7:\n " + eval(derive(exp, "x"), env))
    println("Derivative relative to x at x=5, y=7:\n " + eval(derive(exp, "y"), env))
  }
}