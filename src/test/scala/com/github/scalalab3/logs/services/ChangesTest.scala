package com.github.scalalab3.logs.services

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.TestProbe
import com.github.scalalab3.logs.common.Test
import com.github.scalalab3.logs.storage.LogStorageComponentImpl
import com.github.scalalab3.logs.storage.rethink.RethinkContext
import com.github.scalalab3.logs.storage.rethink.config.RethinkConfig
import com.github.scalalab3.logs.tests.GenLog
import org.specs2.mutable.Specification

import scala.util.Try

class ChangesTest extends Specification {

  val tryRethinkContext = Try(new RethinkContext(RethinkConfig(Test)))

  if (tryRethinkContext.isSuccess) {

    implicit val r = tryRethinkContext.get
    val storage = new LogStorageComponentImpl {
      override val logStorage: LogStorage = new LogStorageImpl()
    }

    implicit val system = ActorSystem("logs-service")
    implicit val mat = ActorMaterializer()
    val wsActor = TestProbe()
    system.actorOf(ChangesActor.props(storage, wsActor.ref), "db-actor")

    "ChangesActor Test" >> {
      val log = GenLog.randomLog()
      storage.logStorage.insert(log)
      wsActor.expectMsg(log) must_== log
    }

  } else "Skipped Test" >> skipped("RethinkContext is not available in ")

}
