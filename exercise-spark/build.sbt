val sparkVersion = "1.4.0"

lazy val commonSettings = Seq(
  organization := "me.shu.exercise.spark",
  version := "0.1.0",
  scalaVersion := "2.10.5",
  libraryDependencies += "org.apache.spark" % "spark-core_2.10" % sparkVersion,
  libraryDependencies += "org.apache.spark" % "spark-mllib_2.10" % sparkVersion,
  libraryDependencies += "org.apache.spark" % "spark-sql_2.10" % sparkVersion,
  libraryDependencies += "org.apache.spark" % "spark-graphx_2.10" % sparkVersion,
  libraryDependencies += "org.apache.spark" % "spark-hive_2.10" % sparkVersion,
  libraryDependencies += "org.apache.spark" % "spark-streaming_2.10" % sparkVersion,
  libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.2" % "test"
)

lazy val exerciseSpark = (project in file(".")).
  aggregate(exerciseSparkUtil, exerciseSparkBasic, exerciseSparkHive, exerciseSparkMLLib, exerciseSparkGraphx, exerciseSparkSQL, exerciseSparkStreaming).
  settings(commonSettings: _*).
  settings(
    name := "exercise-spark"
  )

lazy val exerciseSparkBasic = (project in file("exercise-spark-basic")).
  dependsOn(exerciseSparkUtil).
  settings(commonSettings: _*).
  settings(
    name := "exercise-spark-basic"
  )

lazy val exerciseSparkHive = (project in file("exercise-spark-hive")).
  dependsOn(exerciseSparkUtil).
  settings(commonSettings: _*).
  settings(
    name := "exercise-spark-hive"
  )

lazy val exerciseSparkMLLib = (project in file("exercise-spark-mllib")).
  dependsOn(exerciseSparkUtil).
  settings(commonSettings: _*).
  settings(
    name := "exercise-spark-mllib"
  )

lazy val exerciseSparkGraphx = (project in file("exercise-spark-graphx")).
  dependsOn(exerciseSparkUtil).
  settings(commonSettings: _*).
  settings(
    name := "exercise-spark-graphx"
  )
  
lazy val exerciseSparkSQL = (project in file("exercise-spark-sql")).
  dependsOn(exerciseSparkUtil).
  settings(commonSettings: _*).
  settings(
    name := "exercise-spark-sql"
  )
  
lazy val exerciseSparkStreaming = (project in file("exercise-spark-streaming")).
  dependsOn(exerciseSparkUtil).
  settings(commonSettings: _*).
  settings(
    name := "exercise-spark-streaming"
  )

lazy val exerciseSparkUtil = (project in file("exercise-spark-util")).
  settings(commonSettings: _*).
  settings(
    name := "exercise-spark-util"
  )