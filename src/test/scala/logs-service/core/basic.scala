package com.github.scalalab3_logs.logs_service.core
import scala.concurrent.duration._

import akka.actor._
import akka.testkit._
import org.specs2.mutable._

import com.github.scalalab3_logs.logs_service._


abstract class AkkaContext extends TestKit(ActorSystem())
    with After with ImplicitSender {
  def after = system.terminate()
}

class TestDestination (replyTo: ActorRef) extends Actor {
  def receive = {
    case msg => replyTo ! msg
  }
}

class TestInput (dest: Seq[ActorRef]) extends LogInput {
  val destinations = dest

  def receive = {
    case msg:String => { emit(msg) }
    case (headers:Headers @unchecked, message:String) => { emit(headers, message) }
  }
}

class InputTest extends DefaultSpec {
  "Test LogMessage" in {
    // match case class example
    val l = LogMessage(System.currentTimeMillis, Map(), Map())
    l must matchA[LogMessage]
  }

  "Test input actor" in new AkkaContext {
    val dstRef = system.actorOf(Props(classOf[TestDestination], testActor))
    val inputRef = system.actorOf(Props(classOf[TestInput], Seq(dstRef)))

    val headers = Map("test_header" -> 1)
    val msg = "Test message"

    inputRef ! msg
    receiveOne(500 millis) match {
      case LogMessage(_, _, body) => {
        body("body") must_== msg
      }
    }

    inputRef ! ((headers, msg))
    receiveOne(500 millis) match {
      case LogMessage(_, headers, body) => {
        body("body") must_== msg
      }
    }
  }
}
