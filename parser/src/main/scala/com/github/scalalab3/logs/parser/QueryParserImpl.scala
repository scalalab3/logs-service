package com.github.scalalab3.logs.parser

import com.codecommit.gll
import com.codecommit.gll.RegexParsers
import com.github.scalalab3.logs.common.query._

import scala.reflect.runtime.universe._
import scala.util.{Failure, Success, Try}

class QueryParserImpl[A : TypeTag] extends QueryParser with RegexParsers {

  private def classFields = typeOf[A].members.collect {
    case m: MethodSymbol if m.isCaseAccessor => m.name.toString
  }.toList

  // %%

  lazy val expr = boolQ | mapQ

  lazy val mapQ: Parser[Query] = (
        field ~ "=" ~ value        ^^ { (e1, _, e2) => Eq(e1, e2) }
      | field ~ "!=" ~ value       ^^ { (e1, _, e2) => Neq(e1, e2) }
      | field ~ "contains" ~ value ^^ { (e1, _, e2) => Contains(e1, e2) }
    )

  lazy val boolQ: Parser[Query] = (
        expr ~ "AND" ~ expr ^^ { (e1, _, e2) => And(List(e1, e2)) }
      | expr ~ "OR" ~ expr  ^^ { (e1, _, e2) => Or(List(e1, e2)) }
    )

  lazy val field: Parser[String] = classFields.reduce(_ + "|" + _).r ^^ { s => s }

  val valueReg = "\\'([a-zA-Z0-9 -]*)\\'".r
  lazy val value: Parser[String] = valueReg ^^ { case valueReg(s) => s }

  lazy val fail = Failure(new RuntimeException("Wrong query"))

  // %%

  override def parse(query: String): Try[Query] = {
    val results = expr (query)

    if (results.exists(_.isInstanceOf[gll.Success[Query]])) {
      val qs = for (gll.Success(obj, _) <- results) yield obj
      Success(qs.head)
    } else {
      fail
    }
  }
}
