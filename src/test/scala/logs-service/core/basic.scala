package com.github.scalalab3_logs.logs_service.core
import scala.concurrent.duration._

import akka.actor.{Actor, ActorRef, Props}

import com.github.scalalab3_logs.logs_service._



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

class LogMessageTest extends DefaultSpec {
  "Test LogMessage" >> {
    "case class matching example" >> {
      // match case class example
      val l = LogMessage(System.currentTimeMillis, Map(), Map())
      l must matchA[LogMessage]
    }
  }
}


class InputTest extends AkkaSpec {
  "Test input actor" >> {
    val dstRef = system.actorOf(Props(classOf[TestDestination], testActor))
    val inputRef = system.actorOf(Props(classOf[TestInput], Seq(dstRef)))

    val headers = Map("test_header" -> 1)
    val msg = "Test message"

    "only message" in {
      inputRef ! msg
      receiveOne(500 millis) match {
        case LogMessage(_, _, body) => {
          body("body") must_== msg
        }
      }
    }

    "with headers" in {
      inputRef ! ((headers, msg))
      receiveOne(500 millis) match {
        case LogMessage(_, headers, body) => {
          body("body") must_== msg
        }
      }
    }
  }
}
