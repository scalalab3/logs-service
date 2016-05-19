package com.github.scalalab3.logs.json

import java.time.OffsetDateTime
import java.util.UUID

import com.github.scalalab3.logs.common.Level
import spray.json._

import scala.util.{Failure, Success, Try}

object JsonFormatImplicits {

  implicit object LevelJsonFormat extends RootJsonFormat[Level] {

    def write(x: Level) = JsString(x.toString)

    def read(value: JsValue) = value match {
      case JsString(x) => Level.valueOfCaseSensitive(x) match {
        case Some(level) => level
        case None => deserializationError("Expected Level as JsString, but got " + x)
      }
      case x => deserializationError("Expected Level as JsString, but got " + x)
    }

  }

  implicit object OffsetDateTimeJsonFormat extends RootJsonFormat[OffsetDateTime] {

    def write(x: OffsetDateTime) = JsString(x.toString)

    def read(value: JsValue) = value match {
      case JsString(x) => Try(OffsetDateTime.parse(x)) match {
        case Success(t) => t
        case Failure(e) => deserializationError(s"Expected OffsetDateTime as JsString, but got $x", e)
      }
      case x => deserializationError("Expected OffsetDateTime as JsString, but got " + x)
    }

  }

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {

    def write(x: UUID) = JsString(x.toString)

    def read(value: JsValue) = value match {
      case JsString(x) => Try(UUID.fromString(x)) match {
        case Success(v) => v
        case Failure(e) => deserializationError(s"Expected UUID as JsString, but got $x")
      }
      case x => deserializationError(s"Expected UUID as JsString, but got $x")
    }

  }

}
