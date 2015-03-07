lazy val commonSettings = Seq(
  organization := "me.shu.exercise.scala",
  version := "0.1.0",
  scalaVersion := "2.10.4"
)

lazy val exerciseScala = (project in file(".")).
  aggregate(exerciseScalaBasic).
  settings(commonSettings: _*).
  settings(
    name := "exercise-scala"
  )

lazy val exerciseScalaBasic = (project in file("exercise-scala-basic")).
  settings(commonSettings: _*).
  settings(
    name := "exercise-scala-basic",
	libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.2" % "test"
  )

