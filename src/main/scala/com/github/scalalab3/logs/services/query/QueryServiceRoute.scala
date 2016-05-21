package com.github.scalalab3.logs.services.query

import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import com.github.scalalab3.logs.json.LogJsonImplicits._
import com.github.scalalab3.logs.services._
import spray.http.StatusCodes
import spray.httpx.marshalling.ToResponseMarshallable
import spray.routing.HttpService

import scala.concurrent.duration._

trait QueryServiceRoute extends HttpService {
  self: StorageProvider =>

  implicit val timeout = Timeout(5.seconds)
  implicit def executionContext = actorRefFactory.dispatcher

  val queryActor = actorRefFactory.actorOf(Props[QueryServiceActor], "query-actor")

  val queryRoute = get {
    (path("query") & parameter('query.?)) { string =>
      complete {
        (queryActor ? RequestStorage(Request(string), dbService))
          .mapTo[AbstractResponse].map(handler)
      }
    }
  }

  def handler(x: AbstractResponse): ToResponseMarshallable = x match {
    case BadRequest(error)  => (StatusCodes.BadRequest, error)
    case LogsResponse(logs) => (StatusCodes.OK, logs)
  }
}