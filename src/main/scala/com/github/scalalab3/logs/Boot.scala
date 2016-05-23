package com.github.scalalab3.logs

import akka.actor.{ActorSystem, Props}
import com.github.scalalab3.logs.services.SystemActor

object Boot extends App {
  implicit val system = ActorSystem("logs-service")
  system.actorOf(Props(classOf[SystemActor]), "system-actor")
  println("Call boot")
}