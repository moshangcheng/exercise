import me.shu.exercise.spark.util.Utils
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{Matchers, BeforeAndAfterAll, FlatSpec}

/**
 * Created by moshangcheng on 2015/6/14.
 */
class PartitionNumberTest extends FlatSpec with BeforeAndAfterAll with Matchers {

  private var sc: SparkContext = _

  override def beforeAll(): Unit = {
    Utils.initializeHadoop()
    sc = new SparkContext("local", "PartitionNumberTest", new SparkConf())
  }

  override def afterAll(): Unit = {
    sc.stop()
  }

  "default partition number" should "equals 32" in {
    println(sc.makeRDD(1 to 10).partitions.length)
  }
}
