package com.github.scalalab3.logs.services.query

import akka.actor.Actor
import com.github.scalalab3.logs.parser.QueryParserImpl._
import com.github.scalalab3.logs.services.messages._

import scala.util.{Failure, Success}

class QueryServiceActor extends Actor {

  override def receive: Receive = {
    case StorageRequest(storage, Request(string)) =>
      string.map(parse) match {
        case None => sender ! BadRequest("Empty query")
        case Some(query) => query match {
          case Success(q) => storage ! QuerySender(q, sender)
          case Failure(e) => sender ! BadRequest(e.getMessage)
        }
      }
  }
}
