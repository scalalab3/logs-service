package com.github.scalalab3.logs.services

import java.net.InetSocketAddress
import scala.util.Try

import akka.actor.{ActorRef, Props}
import akka.io.{IO, Tcp}
import play.api.libs.json.{Json, JsValue}

import com.github.scalalab3.logs.common.Log


trait BaseListener extends AbstractActor {
  val output: ActorRef

  def writeLog(msg:Log) = output ! msg
}


class LineReceiver (override val output:ActorRef) extends BaseListener {
  import Tcp._
  import com.github.scalalab3.logs.common_macro._

  def receive = {
    case Received(data) => parseLine(data.utf8String)
    case PeerClosed => context stop self
    case ErrorClosed(cause) => context stop self
  }

  def parseLine(line:String) = Try(Json.parse(line))
    .map(materialize[Log](_).foreach(writeLog _))
}

class TCPListener(host:String, port:Int, out:ActorRef) extends AbstractActor {
  import context.system
  import Tcp._

  log.info(s"Bind to ${host}:${port}")
  IO(Tcp) ! Bind(self, new InetSocketAddress(host, port))

  def receive = {
    case bound@Bound(localAddress) => out ! Ready
    case connected@Connected(remote, local) => {
      val handler = context.actorOf(Props(classOf[LineReceiver], out))
      val conn = sender()
      conn ! Register(handler)
    }
    case CommandFailed(msg: Bind) => {
      log.warning(s"Bind failed: $msg")
      context stop self
    }
  }
}
