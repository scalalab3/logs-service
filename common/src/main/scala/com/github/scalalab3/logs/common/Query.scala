package com.github.scalalab3.logs.common

sealed trait Query

case class And(left: Query, right: Query) extends Query
case class Or(left: Query, right: Query) extends Query
case class Eq(key: String, value: Any) extends Query
case class Neq(key: String, value: Any) extends Query
case class Contains(key: String, value: Any) extends Query

object Query {
  implicit class QueryOp(left: Query) {
    def or(right: Query)  = Or(left, right)
    def and(right: Query) = And(left, right)
  }
}