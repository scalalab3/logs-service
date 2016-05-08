package com.github.scalalab3.logs.common_macro

import java.util

import shapeless._
import shapeless.record._

object ToMap {

  implicit def mapToHashMap(m: Map[String, Any]): util.HashMap[String, Any] = {
    val out: util.HashMap[String, Any] = new util.HashMap()
    m.foreach(kv => out.put(kv._1, kv._2))
    out
  }

  implicit def toHashMap[T, R <: HList, K <: Symbol, V](a: T)
                                                       (implicit
                                                        lgen: LabelledGeneric.Aux[T, R],
                                                        toMap: ops.record.ToMap.Aux[R, K, V],
                                                        converter: Converter[T]
                                                       ): util.HashMap[String, Any] = {
    val gen = LabelledGeneric[T]
    gen.to(a).toMap.filter { case (_, None) => false; case _ => true }
      .map (converter.toMap)
  }
}
