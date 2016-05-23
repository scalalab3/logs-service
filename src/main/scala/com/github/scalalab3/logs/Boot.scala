package com.github.scalalab3.logs

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout
import com.github.scalalab3.logs.config.WebConfig
import com.github.scalalab3.logs.services._
import spray.can.Http

import scala.concurrent.duration._

object Boot extends App {
  val config = WebConfig()

  implicit val timeout = Timeout(5.seconds)
  implicit val system = ActorSystem("logs-service")

  val systemActor = system.actorOf(Props[SystemActor], "system-actor")

  IO(Http) ! Http.Bind(systemActor, interface = config.host, port = config.port)

  println("Call boot")
}
