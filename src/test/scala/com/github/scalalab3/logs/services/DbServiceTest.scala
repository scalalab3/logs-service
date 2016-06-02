package com.github.scalalab3.logs.services

import java.util.concurrent.TimeUnit

import akka.actor.Props
import akka.testkit.TestProbe
import com.github.scalalab3.logs.common._
import com.github.scalalab3.logs.common.query.Query
import com.github.scalalab3.logs.storage.LogStorageComponent
import com.github.scalalab3.logs.tests.{AkkaSpec, GenLog}

import scala.concurrent.duration.FiniteDuration

class DbServiceTest extends AkkaSpec {

  import Offset._

  val randomLog = GenLog.randomLog()

  val logs = {
    val seq = for (_ <- 1 to 5) yield GenLog.randomLog()
    seq.toList
  }

  val logs2 = {
    val seq = for (_ <- 1 to 8) yield GenLog.randomLog()
    seq.toList
  }

  private val expectedSlice = Slice(OffsetBound(5, false) to OffsetBound(10, true))

  val storage = new LogStorageComponent {

    override val logStorage: LogStorage = new LogStorage {override def changes(): Iterator[Log] = ???

      override def indexCreate(index: Index): Unit = ???

      override def count(): Long = 15

      override def filter(query: Query): List[Log] = logs2

      override def insert(log: Log): Boolean = {
        assert(log === randomLog)
        true
      }

      override def slice(slice: Slice): List[Log] = {
        assert(slice == expectedSlice)
        logs
      }
    }
  }

  "DbService" >> {

    val dbService = system.actorOf(Props(classOf[DbService], storage), "db-actor")
    val queryActor = system.actorOf(Props(classOf[QueryServiceActor], dbService), "query-actor")

    "query" in {
      val probe = TestProbe()
      probe.send(queryActor, new Request(Some("name contains 'log'")))
      probe.expectMsg(LogsResponse(logs2))
      ok
    }

    "create" in {
      val probe = TestProbe()
      probe.send(dbService, new Create(randomLog))
      ok
    }

    "page" in {
      val probe = TestProbe()
      probe.send(dbService, Page(2, 5))
      probe.expectMsg(PageLogsResponse(logs, 15))
      ok
    }
  }

}
