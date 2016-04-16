package com.github.scalalab3_logs.logs_service

import akka.actor.{ActorSystem}
import akka.testkit.{TestKit, ImplicitSender}
import org.specs2.mutable.{Specification, SpecificationLike, After}
import org.specs2.matcher.{MatcherMacros}

abstract class DefaultSpec extends Specification with MatcherMacros

abstract class AkkaSpec extends TestKit(ActorSystem())
    with ImplicitSender
    with After
    with SpecificationLike
    with MatcherMacros {
  def after = expectNoMsg
  def afterAll = system.terminate
}


class ExampleText extends DefaultSpec {
  "Example test case" >> {
    val i = 1
    "Subcase 1" in {
      i must_== (1)
    }
  }
}
