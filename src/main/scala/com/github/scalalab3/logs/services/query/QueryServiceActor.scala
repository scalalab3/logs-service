package com.github.scalalab3.logs.services.query

import akka.actor.{Actor, ActorRef}
import com.github.scalalab3.logs.parser.QueryParserImpl._
import com.github.scalalab3.logs.services._

import scala.util.{Failure, Success}

class QueryServiceActor(dbService: ActorRef) extends Actor {

  override def receive: Receive = {
    case Request(string) =>
      string.map(parse) match {
        case None => sender ! BadRequest("Empty query")
        case Some(queryTry) => queryTry match {
          case Success(query) => dbService forward query
          case Failure(error) => sender ! BadRequest(error.getMessage)
        }
      }
  }
}
