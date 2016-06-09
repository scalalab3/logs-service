package com.github.scalalab3.logs.services

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.github.scalalab3.logs.storage.LogStorageComponentImpl
import com.github.scalalab3.logs.storage.rethink.RethinkContext
import com.github.scalalab3.logs.storage.rethink.config.RethinkConfig
import com.github.scalalab3.logs.tests.{AkkaSpec, GenLog}

import scala.concurrent.duration._
import scala.util.Try

class ChangesTest extends AkkaSpec {

  val tryRethinkContext = Try(new RethinkContext(RethinkConfig.load()))

  if (tryRethinkContext.isSuccess) {
    "ChangesActor Test" >> {

      implicit val r = tryRethinkContext.get
      val storage = new LogStorageComponentImpl {
        override val logStorage = new LogStorageImpl()
      }

      implicit val system = ActorSystem("test")
      val stream = system.eventStream

      val probe = TestProbe()
      stream.subscribe(probe.ref, classOf[LogChange])

      val dbService = system.actorOf(Props(classOf[DbService], storage), "db-service")
      system.actorOf(Props(classOf[ChangesActor], dbService), "changes-actor")

      val log = GenLog.randomLog()
      storage.logStorage.insert(log)
      probe.expectMsg(5.seconds, LogChange(log))
      ok
    }

  } else "Skipped Test" >> skipped("RethinkContext is not available in ")

}