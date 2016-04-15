package com.github.scalalab3_logs.logs_service.core
import akka.actor._


case class LogMessage (
  timestamp: Long,
  headers: Map[String, Any],
  body: Map[String, Any]
)


trait LogInput extends Actor {
  type Headers = Map[String, Any]
  type Body = Map[String, Any]

  val destinations: Seq[ActorRef]

  def emit(message: String):Unit = {
    val msg = LogMessage(System.currentTimeMillis, Map(), Map("body" -> message))
    emit(msg)
  }

  def emit(headers: Headers, message:String):Unit = {
    val msg = LogMessage(System.currentTimeMillis, headers, Map("body" -> message))
    emit(msg)
  }

  def emit(message: LogMessage):Unit = {
    destinations.foreach { _ ! message }
  }
}
