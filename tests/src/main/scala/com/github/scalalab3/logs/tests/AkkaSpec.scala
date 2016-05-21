package com.github.scalalab3.logs.tests

import akka.actor.{ActorSystem, DeadLetter}
import akka.testkit.{ImplicitSender, TestKit}
import org.specs2.matcher.MatcherMacros
import org.specs2.mutable.SpecificationLike
import org.specs2.specification.{AfterEach, AfterAll}

abstract class AkkaSpec extends TestKit(ActorSystem())
    with ImplicitSender
    with AfterEach
    with AfterAll
    with SpecificationLike
    with MatcherMacros {

  system.eventStream.subscribe(testActor, classOf[DeadLetter])

  def after = expectNoMsg

  def afterAll() = system.terminate
}
