package com.github.scalalab3.logs.services

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout
import com.github.scalalab3.logs.config.SprayConfig
import spray.can.Http
import scala.concurrent.duration._

object Boot extends App {
  val config = SprayConfig.load()

  implicit val timeout = Timeout(5.seconds)
  implicit val system = ActorSystem("logs-service")

  val systemActor = system.actorOf(Props[SystemActor], "system-actor")

  IO(Http) ! Http.Bind(systemActor, interface = config.host, port = config.port)
}
