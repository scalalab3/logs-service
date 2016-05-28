package com.github.scalalab3.logs.http

import akka.actor.ActorRef
import akka.util.Timeout
import com.github.scalalab3.logs.common.json.LogJsonImplicits._
import com.github.scalalab3.logs.services.{AbstractResponse, BadRequest, LogsResponse, PageLogsResponse}
import spray.http.HttpHeaders.RawHeader
import spray.http.{HttpEntity, HttpResponse, StatusCodes}
import spray.httpx.marshalling.ToResponseMarshallable
import spray.routing.HttpService

import scala.concurrent.Future
import scala.concurrent.duration._

trait AbstractHttpService extends HttpService {

  implicit val timeout = Timeout(5.seconds)

  implicit def executionContext = actorRefFactory.dispatcher

  def abstractService: ActorRef

  def pass(future: Future[Any]): Future[ToResponseMarshallable] = future.mapTo[AbstractResponse].map(handler)

  def handler(x: AbstractResponse): ToResponseMarshallable = x match {
    case BadRequest(error) => (StatusCodes.BadRequest, error)
    case LogsResponse(logs) => (StatusCodes.OK, logs: HttpEntity)
    case PageLogsResponse(logs, count) => HttpResponse(status = StatusCodes.OK, entity = logs,
      headers = List(RawHeader("X-Total", count.toString)))
    case _ => StatusCodes.BadRequest
  }
}
