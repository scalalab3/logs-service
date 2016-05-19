package com.github.scalalab3.logs.json

import com.github.scalalab3.logs.common.Log
import spray.http.ContentTypes.`application/json`
import spray.httpx.marshalling.Marshaller
import spray.json.{RootJsonFormat, DefaultJsonProtocol}

import spray.json._

object LogJsonProtocol extends DefaultJsonProtocol {

  import JsonFormatImplicits._

  implicit val format: RootJsonFormat[Log] = jsonFormat8(Log)

  private val seqToJson: Seq[Log] => String = _.map(_.toJson).mkString("[", ",", "]")

  implicit val marshaller = Marshaller.delegate[Seq[Log], String](`application/json`)(seqToJson)
}
