package com.github.scalalab3.logs.storage.rethink

import java.time.OffsetDateTime
import java.util.UUID

import com.github.scalalab3.logs.common.Log
import com.github.scalalab3.logs.storage.{LogStorageComponent, LogStorageComponentImpl}
import org.specs2.mutable.Specification
import org.specs2.specification._

class LogStorageComponentImplTest extends Specification with BeforeAfterAll {
  sequential

  def uuid = Some(UUID.randomUUID())
  def now = OffsetDateTime.now()

  val log1 = Log(id = uuid, level = 0, env = "test", name = "log1", timestamp = now.minusMinutes(10),
    message = "message1", cause = "unknown", stackTrace = "some cause")

  val log2 = Log(id = uuid, level = 1, env = "test", name = "log2", timestamp = now.minusMinutes(5),
    message = "message2", cause = "empty", stackTrace = "is empty")

  val log3 = Log(id = uuid, level = 1, env = "new", name = "log3", timestamp = now.minusMinutes(1),
    message = "message3", cause = "empty", stackTrace = "null")

  val log4 = Log(id = uuid, level = 2, env = "new", name = "log4", timestamp = now,
    message = "message4", cause = "unknown", stackTrace = "stackTrace")

  val logs = List(log1, log2, log3)

  val storage: LogStorageComponent#LogStorage = new LogStorageComponentImpl {}.logStorage

  "LogStorageComponentImpl Test" in {

    "count logs" in {
      logs foreach storage.insert
      storage.count() must_== 3
    }

    "insert log" in {
      storage.insert(log4)
      storage.count() must_== 4
    }

    "find last N logs" in {
      storage.lastLogs(1).size must_== 1
      storage.lastLogs(3).size must_== 3
      storage.lastLogs(10).size must_== 4
    }

    "filter logs" in {
      storage.filter("name contains 'log'").size must_== 4
      storage.filter("env contains 'test' AND cause = 'unknown'") must_== List(log1)
      storage.filter("any") must_== Nil
      storage.filter("level != '0' AND env = 'test' OR stackTrace = 'stackTrace'").sortBy(_.timestamp) must_== List(log2, log4)
      storage.filter(s"timestamp = '${log4.timestamp}'") must_== List(log4)
    }

  }

  override def beforeAll(): Unit = RethinkContext.dropWork()
  override def afterAll(): Unit = RethinkContext.dropWork()
}