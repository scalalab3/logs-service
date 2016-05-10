package com.github.scalalab3.logs.storage.rethink

import com.github.scalalab3.logs.common._
import org.specs2.mutable.Specification

class QueryToReqlFunction1Test extends Specification {

  "StringQueryToReqlFunction1Test" should {

    val q2r = QueryToReqlFunction1

    "be SuccessfulTry" in {
      q2r(Eq("env", "some val")) must beSuccessfulTry
      q2r(Contains("name", "log")) must beSuccessfulTry
      q2r(null) must beSuccessfulTry
      q2r(And(null, Neq(null, "123"))) must beSuccessfulTry
    }
  }
}
