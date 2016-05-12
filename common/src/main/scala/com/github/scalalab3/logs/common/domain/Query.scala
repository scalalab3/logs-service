package com.github.scalalab3.logs.common.domain

sealed trait Query

case class And(leftQuery: Query, rightQuery: Query) extends Query
case class Or(leftQuery: Query, rightQuery: Query) extends Query

// queries with strings
case class Eq(key: String, value: String) extends Query
case class Neq(key: String, value: String) extends Query
case class Contains(key: String, value: String) extends Query

// queries with time periods
case class Between(leftPeriod: Period, rightPeriod: Period) extends Query
case class Until(period: Period) extends Query

object Query {
  implicit class QueryExt(leftQuery: Query) {
    def or(rightQuery: Query)  = Or(leftQuery, rightQuery)
    def and(rightQuery: Query) = And(leftQuery, rightQuery)
  }

  val timeKey: String = "dateTime"
}