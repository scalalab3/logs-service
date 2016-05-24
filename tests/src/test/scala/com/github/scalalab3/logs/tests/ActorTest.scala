package com.github.scalalab3.logs.tests

import akka.actor.{Actor, Props}
import com.github.scalalab3.logs.common.Log

import scala.concurrent.duration._

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
      }
    }
  }
}
