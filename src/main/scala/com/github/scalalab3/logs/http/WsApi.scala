package com.github.scalalab3.logs.http

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorContext, ActorLogging}
import akka.event.{EventStream, LoggingAdapter}
import com.github.scalalab3.logs.common._
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import play.api.libs.json._

class WsApi(val port: Int) extends Actor with ActorLogging {

  import com.github.scalalab3.logs.common.json.JsonWrites._
  log.info("WS Api start...")

  implicit val system = context.system
  val stream = system.eventStream

  val socketServer = new SocketServer(port, context, stream, log)
  socketServer.start()
  
  def receive = {
    case changes: LogChanges =>
      socketServer.send(Json.toJson(changes).toString())
    case x => log.warning(s"Unexpected message $x")
  }
}

case class SocketServer(port: Int,
                        ctx: ActorContext,
                        stream: EventStream,
                        log: LoggingAdapter) extends
  WebSocketServer(new InetSocketAddress(port)) {

  override def onOpen(ws: WebSocket, clientHandshake: ClientHandshake) = {
    log.info(s"ws connection open")
  }

  override def onClose(webSocket: WebSocket, code: Int, reason: String, remote: Boolean) = {
    log.info(s"connection close reason: $reason")
  }

  override def onMessage(webSocket: WebSocket, message: String) = {
    log.info(s"message given: $message")
  }

  override def onError(webSocket: WebSocket, exception: Exception) = {
    log.info(s"connection error $exception")
  }

  import scala.collection.JavaConversions._

  def send(message: String) {
    for (ws <- connections()) {
      ws.send(message)
    }
  }
}