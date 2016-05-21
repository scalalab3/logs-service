package com.github.scalalab3.logs.storage.rethink

import java.time.OffsetDateTime
import java.util.UUID

import com.github.scalalab3.logs.common._
import com.github.scalalab3.logs.common.query._
import com.github.scalalab3.logs.storage.LogStorageComponentImpl
import com.github.scalalab3.logs.storage.rethink.config.RethinkConfig
import org.specs2.mutable.Specification
import org.specs2.specification._

import scala.util.Try

class LogStorageComponentImplTest extends Specification with BeforeAfterAll {
  sequential

  val tryRethinkContext = Try(new RethinkContext(RethinkConfig.load()))

  "LogStorageComponentImpl Test" >> {

    if (tryRethinkContext.isSuccess) {

      implicit val r = tryRethinkContext.get
      val now = OffsetDateTime.now()

      val log1 = Log(id = uuid(), level = Debug, env = "test", name = "log1", dateTime = now.minusHours(2L),
        message = "message1", cause = "unknown", stackTrace = "some cause")

      val log2 = Log(id = uuid(), level = Info, env = "test", name = "log2", dateTime = now.minusMinutes(40L),
        message = "message2", cause = "empty", stackTrace = "is empty")

      val log3 = Log(id = uuid(), level = Info, env = "new", name = "log3", dateTime = now.minusMinutes(20L),
        message = "message3", cause = "empty", stackTrace = "null")

      val log4 = Log(id = uuid(), level = Error, env = "new", name = "log4", dateTime = now,
        message = "message4", cause = "unknown", stackTrace = "stackTrace")

      val logs = List(log1, log2, log3)

      val storage = new LogStorageComponentImpl {
        override val logStorage: LogStorage = new LogStorageImpl
      }.logStorage

      "count logs" in {
        storage.count() must_== 0
        logs foreach storage.insert
        storage.count() must_== 3
      }

      "insert log" in {
        storage.insert(log4) must beTrue
        storage.count() must_== 4
        storage.insert(log4) must beFalse
        storage.count() must_== 4
      }

      "find last N logs" in {
        storage.lastLogs(1) must have size 1
        storage.lastLogs(3) must have size 3
        storage.lastLogs(10) must have size 4
      }

      "filter logs by query" in {
        storage.filter(Contains("name", "log")) must have size 4
        storage.filter(Contains("env", "test") and Eq("cause", "unknown")) must contain(exactly(log1))
        storage.filter(null) must_== Nil
        storage.filter(Eq("env", "new") and Neq("level", "Error")) must contain(exactly(log3))
        storage.filter(Neq("level", "Debug") and Eq("env", "test") or Contains("stackTrace", "stackTrace")) must contain(exactly(log2, log4))
        storage.filter(Contains("dateTime", "not a number")) must_== Nil
      }

      "filter logs by query with time" in {
        // 10000 sec
        storage.filter(Until(Period(10000, Sec))) must have size 4

        // 1 min .. 1 h
        storage.filter(Period(1, Min) to Period(1, H)).sortBy(_.dateTime) must contain(exactly(log2, log3))

        // name contains 'log' AND 10 min .. 30 min
        storage.filter(Contains("name", "log") and (Period(10, Min) to Period(30, Min))) must contain(exactly(log3))
      }
    } else "Skipped Test" >> skipped ("RethinkContext is not available in ")
  }

  def uuid() = Some(UUID.randomUUID())
  def drop() = for (r <- tryRethinkContext) r.dbDrop()

  override def beforeAll(): Unit = drop()
  override def afterAll(): Unit = drop()
}
