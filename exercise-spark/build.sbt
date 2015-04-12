lazy val commonSettings = Seq(
  organization := "me.shu.exercise.spark",
  version := "0.1.0",
  scalaVersion := "2.10.4",
  libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.3.0",
  libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.2" % "test"
)

lazy val exerciseSpark = (project in file(".")).
  aggregate(exerciseSparkBasic).
  settings(commonSettings: _*).
  settings(
    name := "exercise-spark"
  )

lazy val exerciseSparkBasic = (project in file("exercise-spark-basic")).
  settings(commonSettings: _*).
  settings(
    name := "exercise-spark-basic"
  )

