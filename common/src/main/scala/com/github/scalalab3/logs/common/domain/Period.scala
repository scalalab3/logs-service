package com.github.scalalab3.logs.common.domain

case class Period(private val amountVal: Long = 0L, timeUnit: TimeUnit = Sec) {
  def amount(): Long = if (amountVal < 0L) 0L else amountVal
}

object Period {
  implicit class BetweenExt(leftPeriod: Period) {
    def to(rightPeriod: Period) = Between(leftPeriod, rightPeriod)
  }
}
