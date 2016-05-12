package com.github.scalalab3.logs.storage.rethink

import java.util.UUID
import com.github.scalalab3.logs.common.domain.Log
import com.github.scalalab3.logs.common_macro.Converter
import shapeless.Typeable

object LogToRethinkModel extends Converter[Log] {
  override def toMap[K <: Symbol, V]: Function[(K, V), (String, Any)] = {
    case (k, Some(v: UUID)) => k.name -> v.toString
    case (k, v) => k.name -> v
  }

  override def fromMap: Function[(String, Option[Any]), Option[Any]] = {
    case ("id", opt) => Option(opt.flatMap(Typeable[String].cast).map(UUID.fromString))
    case (_, opt) => opt
  }
}