package com.github.scalalab3.logs.storage.rethink

import org.specs2.mutable.Specification

class StringQueryToReqlFunction1Test extends Specification {

  "StringQueryToReqlFunction1Test" should {

    val q2r = StringQueryToReqlFunction1

    "must beSuccessfulTry" in {
      q2r("env = 'some val'") must beSuccessfulTry
      q2r("name contains 'log'") must beSuccessfulTry
    }

    "must beFailedTry" in {
      q2r("level eq '123'") must beFailedTry
      q2r("timestamp = 'abc'") must beFailedTry
    }
  }
}
