package com.github.scalalab3.logs


import akka.actor.{Actor, ActorLogging, ActorRef}
import com.github.scalalab3.logs.common.Log


trait BaseListener extends Actor with ActorLogging {
  val output: ActorRef

  def receive = {
    case msg => log.warning(s"Unhandled message: $msg")
  }

  def writeLog(msg:Log) = output ! msg
}
