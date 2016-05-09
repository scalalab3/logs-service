package com.github.scalalab3.logs.common

import java.time.OffsetDateTime
import java.util.UUID

import com.github.scalalab3.logs.common_macro._

import scala.util.Random

object GenLog {

  def pairLogMap(id: Option[UUID] = Some(uuid)) = {
    val timestamp = now
    val map: HM = Map[String, Any](
      "level" -> 0,
      "env" -> "test",
      "name" -> "log",
      "timestamp" -> timestamp,
      "message" -> "test message",
      "cause" -> "cause",
      "stackTrace" -> "stack trace")

    id.foreach(map.put("id", _))

    val log = Log(
      id = id,
      level = 0,
      env = "test",
      name = "log",
      timestamp = timestamp,
      message = "test message",
      cause = "cause",
      stackTrace = "stack trace")
    (log, map)
  }

  def randomLog(): Log =
    Log(id = Some(uuid),
      level = 0,
      env = "test",
      name = s"log$num",
      timestamp = now,
      message = s"message$num",
      cause = "unknown",
      stackTrace = "stack trace")

  private def uuid = UUID.randomUUID()
  private def now = OffsetDateTime.now()
  private def num = Random.nextInt()

}
