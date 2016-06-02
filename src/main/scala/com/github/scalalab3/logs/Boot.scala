package com.github.scalalab3.logs

import akka.actor.{ActorSystem, Props}
import com.github.scalalab3.logs.config.WebConfig
import com.github.scalalab3.logs.http.WsApi
import com.github.scalalab3.logs.services.{ChangesActor, DbService, SystemActor}
import com.github.scalalab3.logs.storage.LogStorageComponentImpl
import com.github.scalalab3.logs.storage.rethink.RethinkContext
import com.github.scalalab3.logs.storage.rethink.config.RethinkConfig

import scala.util.Try

object Boot extends App {

  implicit val system = ActorSystem("logs-service")
  system.actorOf(Props(classOf[SystemActor]), "system-actor")

  val config = WebConfig.load()

  system.actorOf(Props(classOf[WsApi], config), "ws-actor")

  val tryRethinkContext = Try(new RethinkContext(RethinkConfig.load()))

  if (tryRethinkContext.isSuccess) {
    implicit val r = tryRethinkContext.get
    val storage = new LogStorageComponentImpl {
      override val logStorage = new LogStorageImpl()
    }
    val dbService = system.actorOf(Props(classOf[DbService], storage), "db-service")
    system.actorOf(Props(classOf[ChangesActor], dbService), "changes-actor")
  }
}