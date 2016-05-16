package com.github.scalalab3.logs

import akka.actor.{Actor, Props}
import com.github.scalalab3.logs.common.Log
import com.github.scalalab3.logs.tests.{GenLog, AkkaSpec}
import org.specs2.matcher.MatcherMacros
import org.specs2.mutable.Specification

import scala.concurrent.duration._

abstract class DefaultSpec extends Specification with MatcherMacros

class EchoActor extends Actor {
  def receive = {
    case msg => sender() ! msg
  }
}

class ActorTest extends AkkaSpec {
  "Test actor" >> {
    val actorRef = system.actorOf(Props(classOf[EchoActor]))
    val msg = GenLog.randomLog()

    "test echo actor" in {
      actorRef ! msg
      receiveOne(500.millis) match {
        case received: Log =>
          received must_== msg
          received must matchA[Log]
          received must matchA[Log].message(msg.message)
      }
    }
  }
}
