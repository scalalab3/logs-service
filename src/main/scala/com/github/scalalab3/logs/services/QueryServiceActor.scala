package com.github.scalalab3.logs.services

import akka.actor.ActorRef
import com.github.scalalab3.logs.parser.QueryParserImpl._

import scalaz._

class QueryServiceActor(dbService: ActorRef) extends AbstractActor {

  override def receive: Receive = {
    case Request(string) =>
      string.map(parse) match {
        case None => sender ! BadRequest("Empty query")
        case Some(queryTry) => queryTry match {
          case \/-(query) => dbService forward RequestQuery(query)
          case -\/(error) => sender ! BadRequest(error)
        }
      }
  }
}
