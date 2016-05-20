package com.github.scalalab3.logs.json

import com.github.scalalab3.logs.common.{Level, Log}
import play.api.libs.json.{JsString, JsValue, Json, Writes}
import spray.http.ContentTypes.`application/json`
import spray.httpx.marshalling.Marshaller

object LogJsonImplicits {
  implicit val levelJsonFormat = new Writes[Level] {
    override def writes(x: Level): JsValue = JsString(x.toString)
  }

  implicit val logFormat = Json.writes[Log]

  implicit val marshaller = Marshaller.delegate[Seq[Log], String](`application/json`)(Json.toJson(_).toString())
}