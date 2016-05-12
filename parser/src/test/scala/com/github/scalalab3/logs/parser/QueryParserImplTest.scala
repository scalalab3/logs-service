package com.github.scalalab3.logs.parser

import com.github.scalalab3.logs.common.domain._
import org.specs2.mutable.Specification

class QueryParserImplTest extends Specification {

  "QueryParserImpl should" >> {

    val parser = QueryParserImpl

    "be able to return simple query" in {
      parser.parse("message contains 'is empty'") must beSuccessfulTry.withValue(Contains("message", "is empty"))
      parser.parse("env = 'production'") must beSuccessfulTry.withValue(Eq("env", "production"))
    }

    "be able to return combination of simple queries" in {
      parser.parse("id = '123' AND env != 'some val'") must beSuccessfulTry.withValue(
        Eq("id", "123") and Neq("env", "some val")
      )

      parser.parse("name contains 'any' OR cause contains 'null'") must beSuccessfulTry.withValue(
        Contains("name", "any") or Contains("cause", "null")
      )
    }

    // should work like:
    // "p1 AND p2 OR p3" -> ((p1 AND p2) OR p3)
    // "p1 OR p2 AND p3" -> (p1 OR (p2 AND p3))

    "be able to return complex query" in {
      parser.parse("name != 'log' AND env contains 'prod' OR level = '1'") must beSuccessfulTry.withValue(
        (Neq("name", "log") and Contains("env", "prod")) or Eq("level", "1")
      )

      parser.parse("level = '0' OR message contains 'zzz' AND name != 'ff'") must beSuccessfulTry.withValue(
        Eq("level", "0") or (Contains("message", "zzz") and Neq("name", "ff"))
      )
    }

    "be able to return query with time" in {
      parser.parse("1 h .. 6 h") must beSuccessfulTry.withValue(
        Period(1L, Hour) to Period(6L, Hour)
      )

      parser.parse("1800 sec OR name = 'log'") must beSuccessfulTry.withValue(
        Until(Period(1800L, Sec)) or Eq("name", "log")
      )

      parser.parse("15 min .. 30 min AND message contains 'foo'") must beSuccessfulTry.withValue(
        Period(15L, Min) to Period(30L, Min) and Contains("message", "foo")
      )
    }

    "be able to return error" in {
      parser.parse("id eq '123'") must beFailedTry.withThrowable[RuntimeException]("Wrong query")
      parser.parse("name contains 'log' XOR level = '0'") must beFailedTry.withThrowable[RuntimeException]("Wrong query")
      parser.parse("1 D") must beFailedTry.withThrowable[RuntimeException]("Wrong query")
      parser.parse("dateTime contains 'time'") must beFailedTry.withThrowable[RuntimeException]("Wrong query")
    }

  }

}
