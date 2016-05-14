package com.github.scalalab3.logs.common.query

import java.time.temporal.{ChronoUnit, TemporalUnit}

import com.github.scalalab3.logs.common.util.Values

sealed trait TimeUnit {
  val temporalUnit: TemporalUnit
}

case object Sec extends TimeUnit {
  override val temporalUnit: TemporalUnit = ChronoUnit.SECONDS
}

case object Min extends TimeUnit {
  override val temporalUnit: TemporalUnit = ChronoUnit.MINUTES
}

case object H extends TimeUnit {
  override val temporalUnit: TemporalUnit = ChronoUnit.HOURS
}

object TimeUnit extends {
  val values: Seq[TimeUnit] = Seq(Sec, Min, H)
} with Values[TimeUnit]