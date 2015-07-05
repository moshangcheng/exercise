package org.apache.spark.sql.hive

class ClosableHiveContext(parent: HiveContext) {
  def close(): Unit = {
    parent.executionHive.state.close()
  }
}