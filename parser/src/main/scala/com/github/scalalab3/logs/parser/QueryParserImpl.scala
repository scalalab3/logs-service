package com.github.scalalab3.logs.parser

import com.codecommit.gll
import com.codecommit.gll.RegexParsers
import com.github.scalalab3.logs.common.domain.TimeUnit.values
import com.github.scalalab3.logs.common.domain._

import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

object QueryParserImpl extends QueryParser with RegexParsers {

  private val stringKey = s"(?!${Query.timeKey})(\\w+)".r   // exclude `dateTime` field
  private val stringVal = "\\'([\\w\\s]+)\\'".r
  private val numVal = "(\\d+)".r
  private val timeVal = values.map(_.name.toLowerCase) ++ values.map(_.name.toUpperCase)

  // %%

  private lazy val expr: Parser[Query] = boolQ | mapQ

  private lazy val mapQ: Parser[Query] = (
        stringKey ~ "=" ~ stringVal         ^^ { (s1, _, s2) => Eq(s1, s2) }
      | stringKey ~ "!=" ~ stringVal        ^^ { (s1, _, s2) => Neq(s1, s2) }
      | stringKey ~ "contains" ~ stringVal  ^^ { (s1, _, s2) => Contains(s1, s2) }

      | periodQ ~ ".." ~ periodQ ^^ { (p1, _, p2) => p1 to p2 }
      | periodQ                  ^^ { p => Until(p) }
    )

  private lazy val boolQ: Parser[Query] = (
        expr ~ "AND" ~ expr ^^ { (q1, _, q2) => q1 and q2 }
      | expr ~ "OR" ~ expr  ^^ { (q1, _, q2) => q1 or q2 }
    )

  private lazy val periodQ = numVal ~ timeVal ^^ { (num, unit) =>
    Period(num.toLong, values.find(_.name == unit.toLowerCase).get) }

  private implicit def toParser(sq: Seq[String]): Parser[String] = sq.reduce(_ + "|" + _).r ^^ { f => f }
  private implicit def toParser(regex: Regex): Parser[String] = regex ^^ { case regex(s) => s }

  private lazy val fail = Failure(new RuntimeException("Wrong query"))

  // %%

  override def parse(query: String): Try[Query] = {

    Try(expr(query)) match {
      case Success(stream) =>

        val queryStream = for {
          gll.Success(q, _) <- stream
        } yield q

        queryStream.headOption match {
          case Some(q) => Success(q)
          case None => fail
        }

      case Failure(e) => Failure(e)
    }
  }
}