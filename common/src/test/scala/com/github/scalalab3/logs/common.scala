package com.github.scalalab3.logs

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.github.scalalab3.logs.common._
import org.specs2.matcher.MatcherMacros
import org.specs2.mutable.{After, Specification, SpecificationLike}

import scala.concurrent.duration._

abstract class DefaultSpec extends Specification with MatcherMacros

abstract class AkkaSpec extends TestKit(ActorSystem())
  with ImplicitSender
  with After
  with SpecificationLike
  with MatcherMacros {
  def after = expectNoMsg

  def afterAll = system.terminate
}

class EchoActor extends Actor {
  def receive = {
    case msg => sender() ! msg
  }
}

class ActorTest extends AkkaSpec {
  "Test actor" >> {
    val actorRef = system.actorOf(Props(classOf[EchoActor]))
    val msg = Log(
      id = Some(java.util.UUID.randomUUID),
      level = 1,
      env = "",
      name = "test",
      timestamp = java.time.OffsetDateTime.now,
      message = "test message",
      cause = "",
      stackTrace = ""
    )

    "test echo actor" in {
      actorRef ! msg
      receiveOne(500 millis) match {
        case received: Log =>
          received must_== msg
          received must matchA[Log]
          received must matchA[Log].message(msg.message)
      }
    }
  }
}
