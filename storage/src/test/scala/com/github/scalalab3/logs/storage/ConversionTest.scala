package com.github.scalalab3.logs.storage

import com.github.scalalab3.logs.storage.ToMap._
import java.util.HashMap
import org.specs2.mutable.Specification

case class TestClass(name_a: String, name_b: Int)
case class TestClass2(a: String, b: Int, c: Double)

class ConversionTest extends Specification {
  implicit def mapToHashMap[A, B](m: Map[A, B]): HashMap[A, B] = {
    val out:HashMap[A, B] = new HashMap()
    m.foreach(kv => out.put(kv._1, kv._2))
    out
  }
  type HM = HashMap[String, Any]

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
}
