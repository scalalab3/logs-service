package com.github.scalalab3.logs.services.read

import akka.actor.ActorRef
import akka.pattern.ask
import com.github.scalalab3.logs.json.LogJsonImplicits._
import com.github.scalalab3.logs.services._
import spray.http.HttpHeaders.RawHeader
import spray.http.{HttpResponse, StatusCodes}
import spray.httpx.marshalling.ToResponseMarshallable

trait ReadServiceRoute extends AbstractHttpService {

  def readService: ActorRef

  val readRoute = get {
    (pathSingleSlash & parameter('page.as[Int] ? 1) & parameter('per_page.as[Int] ? 100)) { (num, size) =>
      complete(
        (readService ? Page(num, size)).mapTo[AbstractResponse].map(handler)
      )
    }
  }

  def handler(x: AbstractResponse): ToResponseMarshallable = x match {
    case BadRequest(error)  => (StatusCodes.BadRequest, error)
    case LogsResponse(logs) => HttpResponse(
      status = StatusCodes.OK,
      entity = logs,
      headers = List(RawHeader("X-Total", logs.size.toString)))
  }
}
