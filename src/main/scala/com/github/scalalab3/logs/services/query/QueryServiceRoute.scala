package com.github.scalalab3.logs.services.query

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.github.scalalab3.logs.json.LogJsonImplicits._
import com.github.scalalab3.logs.services._
import spray.http.StatusCodes
import spray.httpx.marshalling.ToResponseMarshallable
import spray.routing.HttpService

import scala.concurrent.duration._

trait QueryServiceRoute extends HttpService {

  def queryService: ActorRef

  implicit val timeout = Timeout(5.seconds)
  implicit def executionContext = actorRefFactory.dispatcher

  val queryRoute = get {
    (path("query") & parameter('query.?)) { string =>
      complete {
        (queryService ? Request(string))
          .mapTo[AbstractResponse].map(handler)
      }
    }
  }

  def handler(x: AbstractResponse): ToResponseMarshallable = x match {
    case BadRequest(error)  => (StatusCodes.BadRequest, error)
    case LogsResponse(logs) => (StatusCodes.OK, logs)
  }
}