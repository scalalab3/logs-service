package com.github.scalalab3.logs.common.util

import com.github.scalalab3.logs.common.Log

import scala.reflect.runtime.universe._

object Keys {
  // non string fields of Log
  val id = "id"
  val level = "level"
  val time = "dateTime"

  val stringKeys: Seq[String] = typeOf[Log].members.collect {
    case m: MethodSymbol
      if m.isCaseAccessor & m.typeSignature.finalResultType =:= typeOf[String] => m.name.toString
  }.toSeq
}
