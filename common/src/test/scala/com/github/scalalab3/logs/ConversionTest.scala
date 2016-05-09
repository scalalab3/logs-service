package com.github.scalalab3.logs

import com.github.scalalab3.logs.common.GenLog.pairLogMap
import com.github.scalalab3.logs.common.Log
import com.github.scalalab3.logs.common_macro.ToMap._
import com.github.scalalab3.logs.common_macro._
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

case class TestClass1(a: String, b: Int)
case class TestClass2(a: String, b: Int, c: Double)
case class TestClass3(a: String, b: String)

class ConversionTest extends Specification {

  implicit val defaultConverter = new Converter[Any] {
    override def toMap[K <: Symbol, V]: Function[(K, V), (String, Any)] = {
      case (k, Some(v)) => k.name -> v
      case (k, v) => k.name -> v
    }

    override def fromMap: Function[(String, Option[Any]), Option[Any]] = {
      case ("id", opt) => Option(opt)
      case (_, opt) => opt
    }
  }

  trait testClass1 extends Scope {
    val obj = TestClass1(a = "a", b = 1)
    val map: HM = Map("a" -> "a", "b" -> 1)
  }

  trait testClass2 extends Scope {
    val obj = TestClass2(a = "A", b = 0, c = 2.0)
    val map: HM = Map("a" -> "A", "b" -> 0, "c" -> 2.0)
  }

  trait testClass3 extends Scope {
    val obj = TestClass3(a = "asdf", b = "fdsa")
    val map: HM = Map("a" -> "asdf", "b" -> "fdsa")
  }

  trait genLogSomeId extends Scope {
    val (obj, map) = pairLogMap()
  }

  trait genLogNoneId extends Scope {
    val (obj, map) = pairLogMap(None)
  }
}

class ToHashMapTest extends ConversionTest {

  "TestClass to HashMap" in new testClass1 {
    toHashMap(obj) must_== map
  }

  "TestClass2 to HashMap" in new testClass2 {
    toHashMap(obj) must_== map
  }

  "TestClass3 to HashMap" in new testClass3 {
    toHashMap(obj) must_== map
  }

  "GenLogSomeId to HashMap" in new genLogSomeId {
    toHashMap(obj) must_== map
  }

  "GenLogNoneId to HashMap" in new genLogNoneId {
    toHashMap(obj) must_== map
  }
}

class FromHashMapTest extends ConversionTest {

  "HashMap to TestClass1" in new testClass1 {
    Some(obj) must_== materialize[TestClass1](map)
  }

  "HashMap to TestClass2" in new testClass2 {
    Some(obj) must_== materialize[TestClass2](map)
  }

  "HashMap to TestClass3" in new testClass3 {
    Some(obj) must_== materialize[TestClass3](map)
  }

  "GenLogSomeId from HashMap" in new genLogSomeId {
    Some(obj) must_== materialize[Log](map)
  }

  "GenLogNoneId from HashMap" in new genLogNoneId {
    Some(obj) must_== materialize[Log](map)
  }

  "GenLogSomeId from partial HashMap" in new genLogSomeId {
    map.remove("level")
    materialize[Log](map) must_== None
  }

  "GenLogNoneId from partial HashMap" in new genLogNoneId {
    map.remove("level")
    materialize[Log](map) must_== None
  }
}