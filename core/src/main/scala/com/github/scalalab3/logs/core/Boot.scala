package com.github.scalalab3.logs.core

import akka.actor.{ActorSystem, Props}


object Boot extends App {
  implicit val system = ActorSystem("logs-service")
  system.actorOf(Props(classOf[SystemActor]), "system-actor")
  println("Call boot")
}
