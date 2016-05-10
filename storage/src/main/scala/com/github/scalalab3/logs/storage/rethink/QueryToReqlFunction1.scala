package com.github.scalalab3.logs.storage.rethink

import com.github.scalalab3.logs.common._
import com.rethinkdb.gen.ast.{ReqlExpr, ReqlFunction1}

import scala.util.Try

object QueryToReqlFunction1 extends (Query => Try[ReqlFunction1]) {

  override def apply(query: Query): Try[ReqlFunction1] = Try(new ReqlFunction1 {
    override def apply(arg1: ReqlExpr): AnyRef = toFunction(query)(arg1)
  })

  private def toFunction(query: Query): ReqlExpr => ReqlExpr = {
    query match {
      case q: And       => log => toFunction(q.left)(log) and toFunction(q.right)(log)
      case q: Or        => log => toFunction(q.left)(log) or  toFunction(q.right)(log)
      case q: Eq        => _.g(q.key).eq(q.value)
      case q: Neq       => _.g(q.key).ne(q.value)
      case q: Contains  => _.g(q.key).`match`(q.value)
      case _            => log => null
    }
  }
}