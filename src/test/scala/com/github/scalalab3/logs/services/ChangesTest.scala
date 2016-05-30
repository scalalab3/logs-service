package com.github.scalalab3.logs.services

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import com.github.scalalab3.logs.storage.LogStorageComponentImpl
import com.github.scalalab3.logs.storage.rethink.RethinkContext
import com.github.scalalab3.logs.storage.rethink.config.RethinkConfig
import com.github.scalalab3.logs.tests.{AkkaSpec, GenLog}

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

class ChangesTest extends AkkaSpec {

  val tryRethinkContext = Try(new RethinkContext(RethinkConfig.load()))

  if (tryRethinkContext.isSuccess) {

    implicit val r = tryRethinkContext.get
    val storage = new LogStorageComponentImpl {
      override val logStorage = new LogStorageImpl()
    }

    implicit val system = ActorSystem("test")
    val stream = system.eventStream

    stream.subscribe(self, classOf[LogChange])

    val dbService = system.actorOf(Props(classOf[DbService], storage), "db-service")
    system.actorOf(Props(classOf[ChangesActor], dbService), "changes-actor")

    "ChangesActor Test" >> {
      val log = GenLog.randomLog()
      storage.logStorage.insert(log)
      expectMsg(FiniteDuration(10, TimeUnit.SECONDS), log)
      ok
    }

  } else "Skipped Test" >> skipped("RethinkContext is not available in ")

}