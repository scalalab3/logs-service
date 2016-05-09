package com.github.scalalab3.logs.storage.rethink

import java.time.OffsetDateTime
import java.util.UUID

import com.github.scalalab3.logs.common.Log
import com.github.scalalab3.logs.common_macro._
import com.github.scalalab3.logs.common_macro.ToMap._
import org.specs2.mutable.Specification

class LogToRethinkConverterTest extends Specification {

  implicit val converter = new LogToRethinkConverter

  def toMap(log: Log): HM = toHashMap(log)

  def fromMap(map: HM): Option[Log] = implicitly[FromMap[Log]].fromMap(map)

  val time = OffsetDateTime.now()
  val id = UUID.randomUUID()

  val log = Log(id = Some(id), level = 0, env = "test", name = "log1", timestamp = time,
    message = "message1", cause = "unknown", stackTrace = "stackTrace")
  val rethinkModel: HM = Map[String, Any](
    "id" -> id.toString,
    "level" -> 0L, "env" -> "test", "name" -> "log1",
    "timestamp" -> time, "message" -> "message1",
    "cause" -> "unknown", "stackTrace" -> "stackTrace")

  "Log converts to RethinkModel Test" >> {

    val mapFromLog = toMap(log)
    mapFromLog must_== rethinkModel

    val logFromMap = fromMap(mapFromLog).get
    logFromMap must_== log

    val logFromModel = fromMap(rethinkModel).get
    logFromModel must_== log

    val mapFromLogFromModel = toMap(logFromModel)
    mapFromLogFromModel must_== rethinkModel
  }
}
