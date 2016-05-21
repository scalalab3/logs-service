package com.github.scalalab3.logs.storage.rethink

import java.time.OffsetDateTime

import com.github.scalalab3.logs.common.query._
import com.github.scalalab3.logs.common.util.Keys
import ReqlConstants._
import com.rethinkdb.RethinkDB.r
import com.rethinkdb.gen.ast.{ReqlExpr, ReqlFunction1}

object QueryToReqlFunction1 extends (Query => ReqlFunction1) {

  override def apply(query: Query): ReqlFunction1 = new ReqlFunction1 {
    override def apply(arg1: ReqlExpr): AnyRef = toFunction(query)(arg1)
  }

  private def toFunction(query: Query): ReqlExpr => ReqlExpr = {
    query match {
      case q: And       => log => toFunction(q.leftQuery)(log) and toFunction(q.rightQuery)(log)
      case q: Or        => log => toFunction(q.leftQuery)(log) or  toFunction(q.rightQuery)(log)
      case q: Eq        => _.g(q.key).`eq`(q.value)
      case q: Neq       => _.g(q.key).`ne`(q.value)
      case q: Contains  => _.g(q.key).`match`(q.value)

      case q: Between   => _.g(Keys.time)
        .during(toTime(q.rightPeriod), toTime(q.leftPeriod))
        .optArg(leftBound, closed)
        .optArg(rightBound, closed)

      case q: Until     => _.g(Keys.time)
        .during(toTime(q.period), r.now())
        .optArg(leftBound, closed)
        .optArg(rightBound, closed)

      case _            => log => r.expr(false) // solution for null value
    }
  }

  private def toTime(period: Period) = r.epochTime(OffsetDateTime.now()
    .minus(period.amount, period.timeUnit.temporalUnit)
    .toEpochSecond)
}