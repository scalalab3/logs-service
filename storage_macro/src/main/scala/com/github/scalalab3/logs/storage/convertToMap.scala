package com.github.scalalab3.logs.storage

import java.util.HashMap
import shapeless._, labelled.FieldType, ops.hlist.LeftFolder


object ToMap {

  object fill extends Poly {
    implicit def hnil = use((out:HashMap[String, Any], l: HNil) => out)
    implicit def hlist[K <:Symbol, V](implicit wit: Witness.Aux[K]) =
      use((out:HashMap[String, Any], l: FieldType[K, V]) => {
      out.put(wit.value.name, l)
      out
    })
  }
  implicit def toHashMap[A, L <: HList](a: A)
    (implicit
      gen: LabelledGeneric.Aux[A, L],
      lf: LeftFolder.Aux[L, HashMap[String, Any], fill.type, HashMap[String, Any]]
    ): HashMap[String, Any] = {

    val out:HashMap[String, Any] = new HashMap()
    val b = gen.to(a)
    b.foldLeft(out)(fill)
  }
}
