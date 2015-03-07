package me.shu.exercise.scala.basic

trait Ord {
  def <(that: Any): Boolean
  def <=(that: Any): Boolean = (this < that) || (this == that)
  def >(that: Any): Boolean = !(this <= that)
  def >=(that: Any): Boolean = !(this < that)
}

class DateTrait(y: Int, m: Int, d: Int) extends Ord {
  def year = y
  def month = m
  def day = d

  override def toString(): String = year + "-" + month + "-" + day

  override def equals(that: Any): Boolean =
    that.isInstanceOf[DateTrait] && {
      val o = that.asInstanceOf[DateTrait]
      o.day == day && o.month == month && o.year == year
    }

  def <(that: Any): Boolean = {
    if (!that.isInstanceOf[DateTrait])
      error("cannot compare " + that + " and a Date")
    val o = that.asInstanceOf[DateTrait]
    (year < o.year) ||
      (year == o.year && (month < o.month ||
        (month == o.month && day < o.day)))
  }
}

object DateTrait {
  def main(args: Array[String]) {
    val date1 = new DateTrait(2015, 3, 4)
    val date2 = new DateTrait(2015, 3, 4)
    println(date1 < date2)
    println(date1 == date2)
    println(date1 > date2)
  }
}