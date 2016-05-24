package com.github.scalalab3.logs

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.github.scalalab3.logs.config.WebConfig
import com.github.scalalab3.logs.http.WsApi
import com.github.scalalab3.logs.services.{ChangesActor, SystemActor}
import com.github.scalalab3.logs.storage.LogStorageComponentImpl
import com.github.scalalab3.logs.storage.rethink.RethinkContext
import com.github.scalalab3.logs.storage.rethink.config.RethinkConfig

object Boot extends App {

  implicit val system = ActorSystem("logs-service")
  system.actorOf(Props(classOf[SystemActor]), "system-actor")

  implicit val rethinkContext = new RethinkContext(RethinkConfig.load())
  val storage = new LogStorageComponentImpl {
    override val logStorage: LogStorage = new LogStorageImpl()
  }

  implicit val mat = ActorMaterializer()
  val config = WebConfig.load()

  val wsActor: ActorRef = system.actorOf(Props(classOf[WsApi], config), "ws-actor")

  system.actorOf(ChangesActor.props(storage, wsActor), "db-actor")
  println("Call boot")

}