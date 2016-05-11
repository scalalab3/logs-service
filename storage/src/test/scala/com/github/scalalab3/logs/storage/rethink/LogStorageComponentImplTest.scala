package com.github.scalalab3.logs.storage.rethink

import java.time.OffsetDateTime
import java.util.UUID

import com.github.scalalab3.logs.common._
import com.github.scalalab3.logs.storage.LogStorageComponentImpl
import org.specs2.mutable.Specification
import org.specs2.specification._

import scala.util.Try

class LogStorageComponentImplTest extends Specification with BeforeAfterAll {
  sequential

  val tryRethinkContext = Try(new RethinkContext)

  "LogStorageComponentImpl Test" >> {

    if (tryRethinkContext.isSuccess) {

      implicit val r = tryRethinkContext.get

      val log1 = Log(id = uuid(), level = 0, env = "test", name = "log1", timestamp = now(),
        message = "message1", cause = "unknown", stackTrace = "some cause")

      val log2 = Log(id = uuid(), level = 1, env = "test", name = "log2", timestamp = now(),
        message = "message2", cause = "empty", stackTrace = "is empty")

      val log3 = Log(id = uuid(), level = 1, env = "new", name = "log3", timestamp = now(),
        message = "message3", cause = "empty", stackTrace = "null")

      val log4 = Log(id = uuid(), level = 2, env = "new", name = "log4", timestamp = now().minusMinutes(1L),
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
        storage.insert(log4)
        storage.count() must_== 4
        storage.insert(null)
        storage.count() must_== 4
      }

      "find last N logs" in {
        storage.lastLogs(1).size must_== 1
        storage.lastLogs(3).size must_== 3
        storage.lastLogs(10).size must_== 4
      }

      "filter logs" in {
        storage.filter(Contains("name", "log")).size must_== 4
        storage.filter(Contains("env", "test") and Eq("cause", "unknown")) must_== List(log1)
        storage.filter(null) must_== Nil
        storage.filter(Or(null, Eq("stackTrace", "null"))) must_== List(log3)
        storage.filter(And(null, null)) must_== Nil
        storage.filter(Neq("level", 0) and Eq("env", "test") or Contains("stackTrace", "stackTrace"))
          .sortBy(_.timestamp) must_== List(log4, log2)
        storage.filter(Eq("timestamp", log4.timestamp)) must_== List(log4)
        storage.filter(Contains("timestamp", "not a number")) must_== Nil
      }
    } else "Skipped Test" >> skipped ("RethinkContext is not available in ")
  }

  def uuid() = Some(UUID.randomUUID())
  def now() = OffsetDateTime.now()
  def drop() = for (r <- tryRethinkContext) r.dropWork()

  override def beforeAll(): Unit = drop()
  override def afterAll(): Unit =  drop()
}
