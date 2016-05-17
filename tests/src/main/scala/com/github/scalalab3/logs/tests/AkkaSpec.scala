package com.github.scalalab3.logs.tests

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.specs2.matcher.MatcherMacros
import org.specs2.mutable.{After, SpecificationLike}


abstract class AkkaSpec extends TestKit(ActorSystem())
  with ImplicitSender
  with After
  with SpecificationLike
  with MatcherMacros {
  def after = expectNoMsg

  def afterAll = system.terminate
}
