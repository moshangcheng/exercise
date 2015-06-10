package me.shu.exercise.spark.util

import java.io.File

import org.apache.spark.SparkContext
import org.apache.spark.sql.hive.HiveContext

/**
 * Created by moshangcheng on 2015/6/10.
 */
object Utils {

  def initializeHadoop(): Unit = {
    if (System.getProperty("os.name").toLowerCase.contains("windows")) {
      if (System.getProperty("sun.arch.data.model") == "32") {
        System.setProperty("hadoop.home.dir", getFileInClasspath("Win32"))
      } else {
        System.setProperty("hadoop.home.dir", getFileInClasspath("Win64"))
      }
    }
  }

  def createHiveContext(sc: SparkContext): HiveContext = {

    val hiveRoot = if (System.getProperty("os.name").toLowerCase.contains("windows")) {
      "D:/data/hive-root"
    } else {
      "~/data/hive-root"
    }

    System.setProperty("hive.metastore.warehouse.dir", hiveRoot + "/warehouse")
    System.setProperty("hive.exec.scratchdir", hiveRoot + "/scratchdir")
    System.setProperty("javax.jdo.option.ConnectionURL", "jdbc:derby:;databaseName=" + hiveRoot + "/metadb;create=true")
    new HiveContext(sc)
  }

  def getFileInClasspath(path: String): String = {
    new File(this.getClass.getClassLoader.getResource(path).toURI).getAbsolutePath
  }

}
