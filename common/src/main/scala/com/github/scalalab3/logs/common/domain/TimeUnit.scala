package com.github.scalalab3.logs.common.domain

import java.time.temporal.{ChronoUnit, TemporalUnit}

sealed trait TimeUnit {
  def temporalUnit: TemporalUnit
  def name: String
}

case object Sec extends TimeUnit {
  override def temporalUnit: TemporalUnit = ChronoUnit.SECONDS
  override def name: String = "sec"
}

case object Min extends TimeUnit {
  override def temporalUnit: TemporalUnit = ChronoUnit.MINUTES
  override def name: String = "min"
}

case object Hour extends TimeUnit {
  override def temporalUnit: TemporalUnit = ChronoUnit.HOURS
  override def name: String = "h"
}

object TimeUnit {
  val values: Seq[TimeUnit] = Seq(Sec, Min, Hour)
}