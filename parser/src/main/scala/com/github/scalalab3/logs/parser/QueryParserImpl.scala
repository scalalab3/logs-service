package com.github.scalalab3.logs.parser

import com.codecommit.gll
import com.codecommit.gll.RegexParsers
import com.github.scalalab3.logs.common.Level
import com.github.scalalab3.logs.common.query._
import com.github.scalalab3.logs.common.util.Keys.{level, stringKeys}
import com.github.scalalab3.logs.common.util.Values

import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

import scalaz._
import Scalaz._

object QueryParserImpl extends QueryParser with RegexParsers {

  private val stringVal = "'([\\w\\s]+)'".r

  // %%
  private lazy val expr: Parser[Query] = boolQ | mapQ

  private lazy val mapQ: Parser[Query] = (
        level ~ "="  ~ Level.names.riq    ^^ { (_, _, s2) => Eq(level, find(s2, Level).toString) }
      | level ~ "!=" ~ Level.names.riq    ^^ { (_, _, s2) => Neq(level, find(s2, Level).toString) }

      | stringKeys.rs ~ "=" ~ stringVal         ^^ { (s1, _, s2) => Eq(s1, s2) }
      | stringKeys.rs ~ "!=" ~ stringVal        ^^ { (s1, _, s2) => Neq(s1, s2) }
      | stringKeys.rs ~ "contains" ~ stringVal  ^^ { (s1, _, s2) => Contains(s1, s2) }

      | periodQ ~ ".." ~ periodQ ^^ { (p1, _, p2) => p1 to p2 }
      | periodQ                  ^^ { p => Until(p) }
    )

  private lazy val boolQ: Parser[Query] = (
        expr ~ "AND" ~ expr ^^ { (q1, _, q2) => q1 and q2 }
      | expr ~ "OR"  ~ expr ^^ { (q1, _, q2) => q1 or  q2 }
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

  override def parse(query: String): String \/ Query = Try(expr(query)) match {
    case Success(stream) =>

      val queryStream = for {
        gll.Success(q, _) <- stream
      } yield q

      queryStream.headOption.fold(fail)(_.right[String])

    case Failure(_) => fail
  }

  private val fail = "Wrong query".left[Query]
}