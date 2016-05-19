package com.github.scalalab3.logs.services.query

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.github.scalalab3.logs.common.Log
import com.github.scalalab3.logs.components.QueryServiceComponent
import com.github.scalalab3.logs.json.LogJsonProtocol._
import spray.http._
import spray.routing.HttpService

import scala.concurrent.duration._

trait QueryServiceRoute extends HttpService {
  self: QueryServiceComponent =>

  def dbService: ActorRef

  implicit val timeout = Timeout(5.seconds)
  implicit val exc = actorRefFactory.dispatcher

  val queryRoute = get {
    (path("query") & parameter('query.?)) { stringOption =>
        complete {
          val res = for {
            string <- stringOption
            query  <- queryService.queryOf(string)
          } yield query
          res match {
            case Some(query) => (dbService ? query).mapTo[Seq[Log]]
            case None => (StatusCodes.BadRequest, "Wrong query")
          }
        }
    }
  }
}