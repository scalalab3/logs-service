package com.github.scalalab3.logs.services.query

import akka.actor.ActorRef
import akka.pattern.ask
import com.github.scalalab3.logs.json.LogJsonImplicits._
import com.github.scalalab3.logs.services._
import spray.http.{HttpEntity, StatusCodes}
import spray.httpx.marshalling.ToResponseMarshallable

trait QueryServiceRoute extends AbstractHttpService {

  def queryService: ActorRef

  val queryRoute = get {
    (path("query") & parameter('query.?)) { string =>
      complete {
        (queryService ? Request(string))
          .mapTo[AbstractResponse].map(handler)
      }
    }
  }

  private def handler(x: AbstractResponse): ToResponseMarshallable = x match {
    case BadRequest(error)  => (StatusCodes.BadRequest, error)
    case LogsResponse(logs) => (StatusCodes.OK, logs: HttpEntity)
  }
}