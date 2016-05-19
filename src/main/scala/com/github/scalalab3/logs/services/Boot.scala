package com.github.scalalab3.logs.services

import akka.actor.{ActorSystem, Props}

object Boot extends App {
  // val config = SprayConfig.load()

  // implicit val timeout = Timeout(5.seconds)
  implicit val system = ActorSystem("logs-service")

  val systemActor = system.actorOf(Props(classOf[SystemActor]), "system-actor")

  // val dbActor = system.actorOf(Props(classOf[LogStorageActor]), "db-actor")
  // val queryActor = system.actorOf(Props(classOf[QueryServiceActor], dbActor), "query-actor")

  println("Call boot")
  // IO(Http) ! Http.Bind(queryActor, interface = config.host, port = config.port)
}

