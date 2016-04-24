package com.github.scalalab3.logs.storage

import com.github.scalalab3.logs._, common.Log,
  common_macro.FromMap, storage.ToMap._
import java.util.HashMap
import org.specs2.mutable.Specification


case class TestClass(name_a: String, name_b: Int)
case class TestClass2(a: String, b: Int, c: Double)

class Test extends Specification {
  type HM = HashMap[String, Any]

  implicit def mapToHashMap[A, B](m: Map[A, B]): HashMap[A, B] = {
    val out:HashMap[A, B] = new HashMap()
    m.foreach(kv => out.put(kv._1, kv._2))
    out
  }

  def genLog(id: Option[java.util.UUID]) = {
    val timestamp = java.time.Instant.now()
    val tmp = Map[String, Any](
      "level" -> 0,
      "env" -> "test",
      "name" -> "test name",
      "timestamp" -> timestamp,
      "message" -> "test message",
      "cause" -> "",
      "stackTrace" -> "")

    val hm:HM = id match {
      case None => tmp
      case Some(realId) => tmp + ("id" -> realId)
    }

    val log = Log(
      id=id,
      level=0,
      env="test",
      name="test name",
      timestamp=timestamp,
      message="test message",
      cause="",
      stackTrace="")
    (log, hm)
  }
}

class ConversionTest extends Test {
  "TestClass to HashMap" >> {
    val testObj = TestClass(name_a="a", name_b=1)
    val shouldBe:HashMap[String, Any] = Map("name_a" -> "a", "name_b" -> 1)

    (testObj:HM) must_== shouldBe

    val testObj2 = TestClass(name_a="a", name_b=1)
    val shouldBe2:HashMap[String, Any] = Map("name_a" -> "a", "name_b" -> 1)

    (testObj2:HM) must_== shouldBe2
  }

  "TestClass2 to HashMap" >> {
    val testObj = TestClass2(a="A", b=0, c=2.0)
    val shouldBe:HM = Map("a" -> "A", "b" -> 0, "c" -> 2.0)
    (testObj:HM) must_== shouldBe
  }

  "Test Log to HashMap" >> {
    val (obj, hm) = genLog(Some(java.util.UUID.randomUUID))
    (obj:HM) must_== hm

    val (obj2, hm2) = genLog(None)
    (obj2:HM) must_== hm2
  }
}


case class TestClass3(a: String, b: String)

class UnpackTest extends Test {

  def materialize[T: FromMap](map: HM) =
    implicitly[FromMap[T]].fromMap(map)

  "from HM to TestClass3" >> {
    val obj = TestClass3("asdf", "fdsa")
    val testHM:HM = Map("a" -> "asdf", "b" -> "fdsa")
    val testObj = materialize[TestClass3](testHM)
    obj must_== testObj
  }

  "Test Log from HashMap" >> {
    val (obj, hm) = genLog(Some(java.util.UUID.randomUUID))
    obj must_== materialize[Log](hm)

    val (obj2, hm2) = genLog(None)
    obj2 must_== materialize[Log](hm2)
  }
}
