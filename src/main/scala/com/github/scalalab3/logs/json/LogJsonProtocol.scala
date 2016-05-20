package com.github.scalalab3.logs.json

import com.github.scalalab3.logs.common.Log
import com.github.scalalab3.logs.json.JsonFormatImplicits._
import spray.http.ContentTypes.`application/json`
import spray.httpx.marshalling.Marshaller
import spray.json._

object LogJsonProtocol extends DefaultJsonProtocol {

  implicit val format: RootJsonFormat[Log] = jsonFormat8(Log)

  implicit val marshaller = Marshaller
    .delegate[Seq[Log], String](`application/json`)(_.map(_.toJson).mkString("[", ",", "]"))
}
