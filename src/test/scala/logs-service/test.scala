package com.github.scalalab3_logs.logs_service
import org.specs2._
import org.specs2.matcher.{MatcherMacros}

abstract class DefaultSpec extends mutable.Specification with MatcherMacros

class ExampleText extends DefaultSpec {
  "Example test case" >> {
    val i = 1
    "Subcase 1" in {
      i must_== (1)
    }
  }
}
