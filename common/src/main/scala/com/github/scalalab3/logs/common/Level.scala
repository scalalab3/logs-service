package com.github.scalalab3.logs.common

import com.github.scalalab3.logs.common.util.Values

sealed trait Level

case object Debug extends Level
case object Info extends Level
case object Warn extends Level
case object Error extends Level

object Level extends {
  val values: Seq[Level] = Seq(Debug, Info, Warn, Error)
} with Values[Level] {

  implicit def fromString(s:String) =  Level.valueOfCaseInsensitive(s) getOrElse Debug
}
