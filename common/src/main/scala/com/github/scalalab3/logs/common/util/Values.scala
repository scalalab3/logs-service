package com.github.scalalab3.logs.common.util

import scala.util.Random
import scalaz.{CaseInsensitive => CI}

trait Values[+T] {
  val values: Seq[T]

  val count: Int = values.size
  val names: Seq[String] = values.map(_.toString)

  private val pValCaseSensitiveName = names.zip(values).toMap
  private val pValCaseInsensitiveName = pValCaseSensitiveName.map(p => CI(p._1) -> p._2)

  def valueOfCaseSensitive(string: String): Option[T] = pValCaseSensitiveName.get(string)
  def valueOfCaseInsensitive(string: String): Option[T] = pValCaseInsensitiveName.get(CI(string))

  def random: T = values(Random.nextInt(count))
}
