package com.github.scalalab3.logs

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.routing.RoundRobinPool
import com.github.scalalab3.logs.common.Index
import com.github.scalalab3.logs.config.WebConfig
import com.github.scalalab3.logs.http.WsApi
import com.github.scalalab3.logs.services._
import com.github.scalalab3.logs.storage.LogStorageComponentImpl
import com.github.scalalab3.logs.storage.rethink.RethinkContext
import com.github.scalalab3.logs.storage.rethink.config.RethinkConfig
import spray.can.Http

import scala.util.{Failure, Success, Try}

object Boot extends App {

  val dn = "dispatchers.one-thread-dispatcher"

  implicit val system = ActorSystem("logs-service")
  system.actorOf(Props(classOf[SystemActor]).withDispatcher(dn), "system-actor")

  val config = WebConfig.load()
  system.actorOf(Props(classOf[WsApi], config).withDispatcher(dn), "ws-actor")

  Try(new RethinkContext(RethinkConfig.load())) match {
    case Success(context) =>
      implicit val r = context
      val storage = new LogStorageComponentImpl {
        override val logStorage = new LogStorageImpl()
      }
      storage.logStorage.indexCreate(Index())

      val dbService = system.actorOf(Props(classOf[DbService], storage)
        .withDispatcher(dn)
        .withRouter(RoundRobinPool(5)), "db-service")
      system.actorOf(Props(classOf[ChangesActor], dbService).withDispatcher(dn), "changes-actor")

      val supervisor = system.actorOf(Props(classOf[SupervisorActor], dbService, dn).withDispatcher(dn), "supervisor-actor")
      val uiService = system.actorOf(Props(classOf[UiService], supervisor).withDispatcher(dn), "ui-actor")

      IO(Http) ! Http.Bind(uiService, interface = config.host, port = config.port)

    case Failure(fail) =>
      println(fail.getMessage)
      system.terminate
      sys.exit(1)
  }

}