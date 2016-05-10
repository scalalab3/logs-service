package com.github.scalalab3.logs.storage.rethink

import com.github.scalalab3.logs.common.{GenLog, Log}
import com.github.scalalab3.logs.common_macro.ToMap._
import com.github.scalalab3.logs.common_macro._
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class LogToRethinkConverterTest extends Specification {

  implicit val converter = new LogToRethinkConverter

  def toMap(log: Log): HM = toHashMap(log)
  def fromMap(map: HM): Option[Log] = materialize[Log](map)

  trait pairLogRethinkModel extends Scope {
    val log = GenLog.randomLog()
    val rethinkModel: HM = Map[String, Any](
      "id" -> log.id.get.toString,  // important
      "level" -> log.level.toLong,  // important
      "env" -> log.env,
      "name" -> log.name,
      "timestamp" -> log.timestamp,
      "message" -> log.message,
      "cause" -> log.cause,
      "stackTrace" -> log.stackTrace)
  }

  "Log converts to RethinkModel Test" in new pairLogRethinkModel {
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
