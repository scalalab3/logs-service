package com.github.scalalab3.logs.services

import com.github.scalalab3.logs.components.QueryServiceComponent
import spray.http._
import spray.routing.HttpService

trait QueryServiceRoute extends HttpService {
  self: QueryServiceComponent =>

  val queryRoute = path("query") {
    get {
      parameter('query.?) { (string) =>
        complete {
          val res = for {
            s <- string
            q <- queryService.queryOf(s)
          } yield q

          res match {
            case Some(_) => StatusCodes.OK
            case None => (StatusCodes.BadRequest, "Wrong query")
          }
        }
      }
    }
  }
}