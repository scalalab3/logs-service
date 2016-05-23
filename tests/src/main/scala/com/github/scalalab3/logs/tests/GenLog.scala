package com.github.scalalab3.logs.tests

import java.time.OffsetDateTime.now
import java.util.UUID
import java.util.UUID.randomUUID

import com.github.scalalab3.logs.common._
import com.github.scalalab3.logs.common_macro._

import scala.util.Random

object GenLog {

  def pairLogMap(idOpt: Option[UUID] = Some(randomUUID())) = {
    val dateTime = now
    val map: HM = Map[String, Any](
      "level" -> Info,
      "env" -> "test",
      "name" -> "log",
      "dateTime" -> dateTime,
      "message" -> "test message",
      "cause" -> "cause",
      "stackTrace" -> "stack trace")

    idOpt.foreach(map.put("id", _))

    val log = Log(
      id = idOpt,
      level = Info,
      env = "test",
      name = "log",
      dateTime = dateTime,
      message = "test message",
      cause = "cause",
      stackTrace = "stack trace")
    (log, map)
  }

  def randomLog(): Log =
    Log(id = Some(randomUUID()),
      level = Level.random,
      env = Environment.random.toString,
      name = s"log${Random.nextInt()}",
      dateTime = now,
      message = "some message",
      cause = "unknown",
      stackTrace = "empty")
}
