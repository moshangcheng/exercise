package me.shu.exercise.spark.sql

import me.shu.exercise.spark.util.Utils
import org.apache.spark.{SparkConf, SparkContext}
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

object JSONDiffer {

  def httpGetJson(url: String, token: String): String = {
    val ws = NingWSClient()

    val futureResult = ws.url(url).withHeaders("AccessToken" -> token).get().map { response => response.body }

    Await.result(futureResult, 10 second)
  }

  def main(args: Array[String]): Unit = {

    Utils.initializeHadoop()

    val sc = new SparkContext("local", "josn-differ", new SparkConf())
    val hc = Utils.createHiveContext(sc)

    val oldToken = "88956b0f6d799437dc8e65fc45b91fa65b084733"
    val newToken = "6e6994f5a3c63842c95f536f5eb84097611bb456"

//    val url = "http://apiv2.yangkeduo.com/v3/newlist"
    val url = "http://apiv2.yangkeduo.com/v2/goods?page=1&size=50"

    val baseline = hc.read.json(sc.makeRDD(Seq(httpGetJson(url, oldToken)), 4))
    baseline.registerTempTable("baseline")

    val actual = hc.read.json(sc.makeRDD(Seq(httpGetJson(url, newToken)), 4))
    actual.registerTempTable("actual")

    hc.udf.register("diff", (name: String, a: String, b: String) => {
      if (a == null && b == null) {
        false
      } else if ((a == null || b == null) || a.compare(b) != 0) {
        true
      } else {
        false
      }
    })

    hc.sql(
      """select
        |  baseline.e.goods_id, actual.e.goods_id
        |from
        |(
        |  select e from baseline lateral view explode(goods_list) t_goods as e
        |) baseline
        |full outer join
        |(
        |  select e from actual lateral view explode(goods_list) t_goods as e
        |) actual
        |on baseline.e.goods_id = actual.e.goods_id
        |where
        |  case
        |    when isnull(baseline.e) or isnull(actual.e) then true
        |    when baseline.e.goods_id <> actual.e.goods_id then true
        |    when baseline.e.goods_name <> actual.e.goods_name then true
        |    else false
        |  end
        | """.stripMargin).toDF().show(1000, true)

    sc.stop()
  }
}
