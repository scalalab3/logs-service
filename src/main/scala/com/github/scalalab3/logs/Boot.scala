package com.github.scalalab3.logs

import akka.actor.{ActorSystem, Props}
import com.github.scalalab3.logs.config.dbConfig
import com.github.scalalab3.logs.config.appConfig
import com.github.scalalab3.logs.services.ChangesActor
import com.github.scalalab3.logs.storage.LogStorageComponentImpl
import com.github.scalalab3.logs.storage.rethink.RethinkContext

object Boot extends App {

  implicit val rethinkContext = new RethinkContext(dbConfig)

  val storage = new LogStorageComponentImpl {
    override val logStorage: LogStorage = new LogStorageImpl()
  }

  implicit val system = ActorSystem("logs-service")
  system.actorOf(Props(classOf[ChangesActor], storage, appConfig.wsPort), "db-actor")

}
