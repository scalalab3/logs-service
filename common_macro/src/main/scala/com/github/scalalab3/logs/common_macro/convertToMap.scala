package com.github.scalalab3.logs.common_macro

import shapeless._
import shapeless.record._

object ToMap {

  implicit def toHashMap[T: Converter, R <: HList, K <: Symbol, V](a: T)
                                                                  (implicit
                                                                   lgen: LabelledGeneric.Aux[T, R],
                                                                   toMap: ops.record.ToMap.Aux[R, K, V]
                                                                  ): HM = {
    lgen.to(a).toMap.filter(_._2.nonEmpty).map(implicitly[Converter[T]].toMap)
  }

  implicit class AnyExt(a: Any) {
    def nonEmpty: Boolean = if (a == None) false else true
  }

}
