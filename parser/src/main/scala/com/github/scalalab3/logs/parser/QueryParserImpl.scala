package com.github.scalalab3.logs.parser

import com.codecommit.gll
import com.codecommit.gll.RegexParsers
import com.github.scalalab3.logs.common.query._
import com.github.scalalab3.logs.common.util.{Keys, Values}
import com.github.scalalab3.logs.common.Level

import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

object QueryParserImpl extends QueryParser with RegexParsers {

  private val stringVal = "'([\\w\\s]+)'".r

  // %%
  private lazy val expr: Parser[Query] = boolQ | mapQ

  private lazy val mapQ: Parser[Query] = (
        Keys.level ~ "="  ~ Level.names.riq    ^^ { (_, _, s2) => Eq(Keys.level, find(s2, Level).toString) }
      | Keys.level ~ "!=" ~ Level.names.riq    ^^ { (_, _, s2) => Neq(Keys.level, find(s2, Level).toString) }

      | Keys.stringKeys.rs ~ "=" ~ stringVal         ^^ { (s1, _, s2) => Eq(s1, s2) }
      | Keys.stringKeys.rs ~ "!=" ~ stringVal        ^^ { (s1, _, s2) => Neq(s1, s2) }
      | Keys.stringKeys.rs ~ "contains" ~ stringVal  ^^ { (s1, _, s2) => Contains(s1, s2) }

      | periodQ ~ ".." ~ periodQ ^^ { (p1, _, p2) => p1 to p2 }
      | periodQ                  ^^ { p => Until(p) }
    )

  private lazy val boolQ: Parser[Query] = (
        expr ~ "AND" ~ expr ^^ { (q1, _, q2) => q1 and q2 }
      | expr ~ "OR" ~ expr  ^^ { (q1, _, q2) => q1 or  q2 }
    )

  private lazy val periodQ = "(\\d+)".r ~ TimeUnit.names.ri ^^ { (n, unit) => Period(n.toLong, find(unit, TimeUnit)) }

  implicit class SeqToParser(seq: Seq[String]) {
    // case sensitive regex from seq
    def rs: Parser[String]  = s"(${seq.mkString("|")})".r

    // case insensitive regex from seq
    def ri: Parser[String]  = s"(?i)(${seq.mkString("|")})".r

    // case insensitive regex from seq with ' around elements
    def riq: Parser[String]  = s"(?i)(${seq.mkString("'", "'|'", "'")})".r ^^ { case stringVal(s) => s }
  }

  private implicit def toParser(regex: Regex): Parser[String] = regex ^^ { case regex(s) => s }

  private def find[T](str: String, vals: Values[T]): T = vals.valueOfCaseInsensitive(str).get

  // %%

  override def parse(query: String): Try[Query] = Try(expr(query)) match {
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

  private lazy val fail = Failure(new RuntimeException("Wrong query"))
}