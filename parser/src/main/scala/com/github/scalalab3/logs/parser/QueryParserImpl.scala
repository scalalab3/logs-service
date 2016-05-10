package com.github.scalalab3.logs.parser

import java.time.OffsetDateTime

import com.codecommit.gll
import com.codecommit.gll.RegexParsers
import com.github.scalalab3.logs.common._

import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

object QueryParserImpl extends QueryParser with RegexParsers {

  // another way ??
  private val stringFs = List("id", "name", "env", "message", "cause", "stackTrace")
  private val intFs = List("level")
  private val dateFs = List("timestamp")

  private val stringVal = "\\'([ a-zA-Z0-9\\-\\.\\+\\:\\,\\;]*)\\'".r

  // %%

  lazy val expr = boolQ | mapQ

  lazy val mapQ: Parser[Query] = (
        stringFs ~ "=" ~ stringVal        ^^ { (e1, _, e2) => Eq(e1, e2) }
      | stringFs ~ "!=" ~ stringVal       ^^ { (e1, _, e2) => Neq(e1, e2) }
      | stringFs ~ "contains" ~ stringVal ^^ { (e1, _, e2) => Contains(e1, e2) }
      | intFs ~ "=" ~ stringVal           ^^ { (e1, _, e2) => Eq(e1, e2.toInt) }
      | intFs ~ "!=" ~ stringVal          ^^ { (e1, _, e2) => Neq(e1, e2.toInt) }
      | dateFs ~ "=" ~ stringVal          ^^ { (e1, _, e2) => Eq(e1, OffsetDateTime.parse(e2)) }
      | dateFs ~ "!=" ~ stringVal         ^^ { (e1, _, e2) => Neq(e1, OffsetDateTime.parse(e2)) }
    )

  lazy val boolQ: Parser[Query] = (
        expr ~ "AND" ~ expr ^^ { (e1, _, e2) => And(e1, e2) }
      | expr ~ "OR" ~ expr  ^^ { (e1, _, e2) => Or(e1, e2) }
    )

  private implicit def toParser(sq: Seq[String]): Parser[String] = sq.reduce(_ + "|" + _).r ^^ { f => f }
  private implicit def toParser(regex: Regex): Parser[String] = regex ^^ { case regex(s) => s }

  lazy val fail = Failure(new RuntimeException("Wrong query"))

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