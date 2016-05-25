package com.github.scalalab3.logs.http

import java.net.InetSocketAddress

import akka.event.LoggingAdapter
import com.github.scalalab3.logs.common._
import com.github.scalalab3.logs.config.WebConfig
import com.github.scalalab3.logs.services.AbstractActor
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import play.api.libs.json._

class WsApi(val config: WebConfig) extends AbstractActor {
  import com.github.scalalab3.logs.common.json.LogJsonImplicits._

  log.info("WS Api start...")

  implicit val system = context.system
  val stream = system.eventStream

  val socketServer = new SocketServer(config.host, config.wsPort, log)
  socketServer.start()

  def receive = {
    case change: Log => socketServer.send(Json.toJson(change).toString())
  }
}

case class SocketServer(host: String,
                        port: Int,
                        log: LoggingAdapter) extends
  WebSocketServer(new InetSocketAddress(host, port)) {

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

  def send(message: String): Unit = {
    for (ws <- connections()) {
      ws.send(message)
    }
  }
}
