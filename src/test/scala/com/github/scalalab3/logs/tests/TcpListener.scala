package com.github.scalalab3.logs

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import com.github.scalalab3.logs.tests.GenLog.randomLog
import com.github.scalalab3.logs.tests.AkkaSpec
import play.api.libs.json.Json
import com.github.scalalab3.logs.common.json.LogJsonImplicits._


sealed trait TestMsg
case object Ok extends TestMsg
case object Nok extends TestMsg
case class Message(msg:String) extends TestMsg


class Client (host:String, port:Int, out:ActorRef) extends Actor {
  import context.system
  import Tcp._

  var connection:Option[ActorRef] = None
  IO(Tcp) ! Connect(new InetSocketAddress(host, port))

  def receive = {
    case CommandFailed(rsn: Connect) =>
      out ! Nok
      context stop self
    case c @ Connected(remote, local) =>
      val conn = sender()
      conn ! Register(self)
      connection = Some(conn)
      out ! Ok
    case c @ Message(msg) =>
      connection.map(conn => {
        conn ! Write(ByteString(msg))
      })
  }
}

class TCPListenerTest extends AkkaSpec {
  sequential  // cannot run parallel tests due to port is hardcoded
  val host = "0.0.0.0"
  val port = 15000
  val log = system.actorOf(Props(classOf[TCPListener], host, port, testActor), "listener")
  val logMsg = randomLog()

  "Listener ready" >> {
    receiveOne(period) match {
      case "ready" => ok
    }
  }

  "Connection test" >> {
    var tc:ActorRef = self

    "Connect" in {
      tc = system.actorOf(Props(classOf[Client], host, port, testActor), "test-client")
      receiveOne(period) match {
        case Ok => ok
      }
    }
    "Send malformed" in {
      val s = "malformed string"
      tc ! Message(s)
      ok
    }
    "Send json string and receive log message" in {
      val j = Json.stringify(Json.toJson(logMsg))
      tc ! Message(j)
      receiveOne(period) match {
        case logMsg => ok
      }
    }
    "Kill" in {
      tc ! PoisonPill
      ok
    }
  }

  override def afterAll = {
    println("Send PoisonPill")
    log ! PoisonPill
    super.afterAll
  }

}
