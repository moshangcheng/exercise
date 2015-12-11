package me.shu.exercise.scala.basic

import org.scalatest.{Matchers, FlatSpec}

import scala.collection.JavaConversions._


class LinkedHashMapTest extends FlatSpec with Matchers {

  "LinkedHashMap" should "pop values in last-in-first-out order" in {

    val cacheSize = 5
    val lruCache = new java.util.LinkedHashMap[Integer, Integer](10, 0.75f, true) {
      protected override def removeEldestEntry(eldest: java.util.Map.Entry[Integer, Integer]): Boolean = {
        return this.size > cacheSize
      }
    }

    (0 until 3) foreach { i => lruCache.put(i, i) }
    lruCache.get(0)
    (3 until 6) foreach { i => lruCache.put(i, i) }

    lruCache.foreach(println)

  }

}