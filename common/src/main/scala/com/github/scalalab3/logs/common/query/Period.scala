package com.github.scalalab3.logs.common.query

case class Period(amount: Long = 0L, timeUnit: TimeUnit = Sec)

object Period {
  implicit class BetweenExt(leftPeriod: Period) {
    def to(rightPeriod: Period) = Between(leftPeriod, rightPeriod)
  }
}
