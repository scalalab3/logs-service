package com.github.scalalab3.logs.storage.rethink

import java.util.UUID

import com.github.scalalab3.logs.common.Log
import com.github.scalalab3.logs.common_macro.Converter

class LogToRethinkConverter extends Converter[Log] {
  override def toMap[K <: Symbol, V]: Function[(K, V), (String, Any)] = {
    case (k, Some(v: UUID)) => k.name -> v.toString
    case (k, v: Number) => k.name -> v.longValue()
    case (k, v) => k.name -> v
  }

  override def fromMap: Function[(String, Option[Any]), Option[Any]] = {
    case ("id", opt) => Option(opt.map(_.asInstanceOf[String]).map(UUID.fromString))
    case ("level", opt) => opt.map(_.asInstanceOf[Long].toInt)
    case (_, opt) => opt
  }
}