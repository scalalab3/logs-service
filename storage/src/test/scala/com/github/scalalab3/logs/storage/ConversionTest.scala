package com.github.scalalab3.logs.storage

import com.github.scalalab3.logs.common_macro.FromMap
import com.github.scalalab3.logs.storage.ToMap._
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
}
