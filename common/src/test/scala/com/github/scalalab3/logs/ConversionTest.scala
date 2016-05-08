package com.github.scalalab3.logs

import java.util

import com.github.scalalab3.logs.common.Log
import com.github.scalalab3.logs.common_macro.FromMap
import com.github.scalalab3.logs.common_macro.ToMap._
import org.specs2.mutable.Specification

case class TestClass(name_a: String, name_b: Int)

case class TestClass2(a: String, b: Int, c: Double)

class Test extends Specification {

  type HM = util.HashMap[String, Any]

  def genLog(id: Option[java.util.UUID] = Some(java.util.UUID.randomUUID)) = {
    val timestamp = java.time.OffsetDateTime.now()
    val map: HM = Map[String, Any](
      "level" -> 0,
      "env" -> "test",
      "name" -> "test name",
      "timestamp" -> timestamp,
      "message" -> "test message",
      "cause" -> "",
      "stackTrace" -> "")

    id.foreach(map.put("id", _))

    val log = Log(
      id = id,
      level = 0,
      env = "test",
      name = "test name",
      timestamp = timestamp,
      message = "test message",
      cause = "",
      stackTrace = "")
    (log, map)
  }
}

class ConversionTest extends Test {

  "TestClass to HashMap" >> {
    val testObj = TestClass(name_a = "a", name_b = 1)
    val shouldBe: HM = Map("name_a" -> "a", "name_b" -> 1)

    toHashMap(testObj) must_== shouldBe

    val testObj2 = TestClass(name_a = "a", name_b = 1)
    val shouldBe2: HM = Map("name_a" -> "a", "name_b" -> 1)

    toHashMap(testObj2) must_== shouldBe2
  }

  "TestClass2 to HashMap" >> {
    val testObj = TestClass2(a = "A", b = 0, c = 2.0)
    val shouldBe: HM = Map("a" -> "A", "b" -> 0, "c" -> 2.0)
    toHashMap(testObj) must_== shouldBe
  }

  "Test Log to HashMap" >> {
    val (obj, hm) = genLog()
    toHashMap(obj) must_== hm

    val (obj2, hm2) = genLog(None)
    toHashMap(obj2) must_== hm2
  }
}

case class TestClass3(a: String, b: String)

class UnpackTest extends Test {

  def materialize[T: FromMap](map: HM): Option[T] = implicitly[FromMap[T]].fromMap(map)

  "HashMap to TestClass3" >> {
    val obj = TestClass3("asdf", "fdsa")
    val testHM: HM = Map("a" -> "asdf", "b" -> "fdsa")
    val testObj = materialize[TestClass3](testHM)
    Some(obj) must_== testObj
  }

  "Test Log from HashMap" >> {
    val (obj, hm) = genLog()
    Some(obj) must_== materialize[Log](hm)

    val (obj2, hm2) = genLog(None)
    Some(obj2) must_== materialize[Log](hm2)
  }

  "Test Log from partial HashMap" >> {
    val (obj, hm) = genLog()
    hm.remove("level")
    materialize[Log](hm) must_== None

    val (obj2, hm2) = genLog(None)
    hm2.remove("level")
    materialize[Log](hm2) must_== None
  }
}