package com.github.scalalab3.logs.common.json

import com.github.scalalab3.logs.common.{Log, _}
import play.api.libs.json.{JsString, JsValue, Json, Writes}

object JsonWrites {

  implicit val jsonEnumWrites = new Writes[Level] {
    override def writes(x: Level): JsValue = {
      x match {
        case Debug => JsString("Debug")
        case Info => JsString("Info")
        case Warn => JsString("Warn")
        case Error => JsString("Error")
      }
    }
  }

  implicit val logsFormat = Json.writes[Log]
  implicit val logFormat = Json.writes[LogChanges]

}
