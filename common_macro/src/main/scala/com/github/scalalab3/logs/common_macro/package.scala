package com.github.scalalab3.logs

import java.util

package object common_macro {

  type HM = util.HashMap[String, Any]

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

}
