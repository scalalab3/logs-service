package com.github.scalalab3.logs.common.json

import com.github.scalalab3.logs.common.{Level, Log}
import play.api.libs.json.{JsString, JsValue, Json, Writes}
import spray.http.ContentTypes.`application/json`
import spray.http.HttpEntity

object LogJsonImplicits {
  implicit val levelJsonFormat = new Writes[Level] {
    override def writes(x: Level): JsValue = JsString(x.toString)
  }

  implicit val logFormat = Json.writes[Log]

  implicit def toJsonHttpEntity(logs: Seq[Log]): HttpEntity = HttpEntity(
      contentType = `application/json`,
      string = Json.toJson(logs).toString()
    )
}
