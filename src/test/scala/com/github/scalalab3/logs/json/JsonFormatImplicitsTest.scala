package com.github.scalalab3.logs.json

import java.time.OffsetDateTime
import java.util.UUID

import com.github.scalalab3.logs.tests.GenLog
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import spray.json.{DeserializationException, JsString}

class JsonFormatImplicitsTest extends Specification {

  val jsLeftVal = JsString("bad value")

  trait RandomUUID extends Scope {
    val toJson = JsonFormatImplicits.UuidJsonFormat
    val v = UUID.randomUUID()
    val jsRightVal = JsString(v.toString)
  }

  trait RandomOffsetDateTime extends Scope {
    val toJson = JsonFormatImplicits.OffsetDateTimeJsonFormat
    val v = OffsetDateTime.now()
    val jsRightVal = JsString(v.toString)
  }

  trait RandomLevel extends Scope {
    val toJson = JsonFormatImplicits.LevelJsonFormat
    val v = GenLog.randomLevel()
    val jsRightVal = JsString(v.toString)
  }

  "UuidJsonFormat should write" in new RandomUUID {
    toJson.write(v).value mustEqual v.toString
  }

  "UuidJsonFormat should read" in new RandomUUID {
    toJson.read(jsRightVal) mustEqual v
    toJson.read(jsLeftVal) must throwA[DeserializationException]
  }

  "OffsetDateTimeJsonFormat should write" in new RandomOffsetDateTime {
    toJson.write(v).value mustEqual v.toString
  }

  "OffsetDateTimeJsonFormat should read" in new RandomOffsetDateTime {
    toJson.read(jsRightVal) mustEqual v
    toJson.read(jsLeftVal) must throwA[DeserializationException]
  }

  "LevelFormat should write" in new RandomLevel {
    toJson.write(v).value mustEqual v.toString
  }

  "LevelFormat should read" in new RandomLevel {
    toJson.read(jsRightVal) mustEqual v
    toJson.read(jsLeftVal) must throwA[DeserializationException]
  }

}
