package com.github.scalalab3.logs.common

import com.github.scalalab3.logs.common.util.Values

sealed trait Environment {
  def toKey = toString.toLowerCase
}

case object Test extends Environment
case object Dev extends Environment
case object Prod extends Environment

object Environment extends {
  val values: Seq[Environment] = Seq(Test, Dev, Prod)
} with Values[Environment]