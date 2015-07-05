package me.shu.exercise.spark.util

import java.io.File

import org.apache.spark.SparkContext
import org.apache.spark.sql.hive.HiveContext

import scala.util.Properties

/**
 * Created by moshangcheng on 2015/6/10.
 */
object Utils {

  def initializeHadoop(): Unit = {
    if (Properties.isWin) {
      val hadoopPath = if (System.getProperty("sun.arch.data.model").equalsIgnoreCase("32")) {
        getFileInClasspath("Win32")
      }
      else {
        getFileInClasspath("Win64")
      }
      System.setProperty("hadoop.home.dir", hadoopPath)
      System.setProperty("hadoop.tmp.dir", "D:/data/hadoop-tmp-dir")
      System.setProperty("java.library.path", hadoopPath + File.separator + "bin;" + System.getProperty("java.library.path"))
      System.load(hadoopPath + File.separator + "bin" + File.separator + "hadoop.dll")
    }
  }

  def createHiveContext(sc: SparkContext): HiveContext = {
    if (Properties.isWin) {
      val hiveRoot = "D:/data/hive-root"
      System.setProperty("hive.metastore.warehouse.dir", hiveRoot + "/warehouse")
      System.setProperty("hive.exec.scratchdir", hiveRoot + "/scratchdir")
      System.setProperty("javax.jdo.option.ConnectionURL", "jdbc:derby:;databaseName=" + hiveRoot + "/metadb;create=true")
    }
    new HiveContext(sc)
  }

  def getFileInClasspath(path: String): String = {
    new File(this.getClass.getClassLoader.getResource(path).toURI).getAbsolutePath
  }

}
