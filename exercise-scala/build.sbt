lazy val commonSettings = Seq(
  organization := "me.shu.exercise.scala",
  version := "0.1.0",
  scalaVersion := "2.10.5",
  libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.1",
  libraryDependencies += "com.github.t3hnar" %% "scalax" % "2.5",
  libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.2" % "test"
)

lazy val exerciseScala = (project in file(".")).
  aggregate(exerciseScalaBasic, exerciseScalaRecommender).
  settings(commonSettings: _*).
  settings(
    name := "exercise-scala"
  )

lazy val exerciseScalaBasic = (project in file("exercise-scala-basic")).
  settings(commonSettings: _*).
  settings(
    name := "exercise-scala-basic"
  )

lazy val exerciseScalaRecommender = (project in file("exercise-scala-recommender")).
  settings(commonSettings: _*).
  settings(
    name := "exercise-scala-recommender"
  )
