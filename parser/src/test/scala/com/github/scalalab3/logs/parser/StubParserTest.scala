package com.github.scalalab3.logs.parser

import com.github.scalalab3.logs.query.Eq
import org.specs2.mutable.Specification


class StubParserTest extends Specification {
  "Stub Parser should" >> {

    val parser = new StubParser

    "be able to return different queries" in {
      parser.parse("field = 'value'") must beSuccessfulTry.withValue(Eq("field", "value"))
      parser.parse("env = 'production'") must beSuccessfulTry.withValue(Eq("env", "production"))
    }

    "be able to return error" in {
      parser.parse("some unknown query") must beFailedTry.withThrowable[RuntimeException]("Wrong query")
    }

  }
}
