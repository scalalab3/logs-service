package com.github.scalalab3.logs.storage.rethink

import com.github.scalalab3.logs.parser.QueryParserImpl
import com.github.scalalab3.logs.query._
import com.rethinkdb.gen.ast.{ReqlExpr, ReqlFunction1}

import scala.util.{Failure, Success, Try}

object StringQueryToReqlFunction1 extends (String => Try[ReqlFunction1]) {

  private val p = QueryParserImpl

  override def apply(s: String): Try[ReqlFunction1] = {
    p.parse(s) match {
      case Success(a) => Try(new ReqlFunction1 {
        override def apply(arg1: ReqlExpr): AnyRef = toFunction(a)(arg1)
      })
      case Failure(e) => Failure(e)
    }
  }

  private def toFunction(query: Query): ReqlExpr => ReqlExpr = {
    query match {
      case q: And       => log => toFunction(q.left)(log) and toFunction(q.right)(log)
      case q: Or        => log => toFunction(q.left)(log) or  toFunction(q.right)(log)
      case q: Eq        => _.g(q.key).eq(q.value)
      case q: Neq       => _.g(q.key).ne(q.value)
      case q: Contains  => _.g(q.key).`match`(q.value)
    }
  }
}