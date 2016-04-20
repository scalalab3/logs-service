package com.github.scalalab3.logs.common.query

sealed trait Query

case class And(subQueries: List[Query]) extends Query
case class Or(subQueries: List[Query]) extends Query
case class Eq(key: String, value: String) extends Query
case class Neq(key: String, value: String) extends Query
case class Contains(key: String, value: String) extends Query