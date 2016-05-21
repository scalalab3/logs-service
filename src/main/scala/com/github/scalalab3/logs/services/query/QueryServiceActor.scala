package com.github.scalalab3.logs.services.query

import com.github.scalalab3.logs.parser.QueryParserImpl._
import com.github.scalalab3.logs.services.{BadRequest, Request, RequestStorage, AbstractService}

import scala.util.{Failure, Success}

class QueryServiceActor extends AbstractService {

  override def receive: Receive = {
    case RequestStorage(Request(string), storage) =>
      string.map(parse) match {
        case None => sender ! BadRequest("Empty query")
        case Some(queryTry) => queryTry match {
          case Success(query) => storage forward query
          case Failure(error) => sender ! BadRequest(error.getMessage)
        }
      }
  }
}
