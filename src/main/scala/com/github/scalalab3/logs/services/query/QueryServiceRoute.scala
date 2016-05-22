package com.github.scalalab3.logs.services.query

import akka.actor.ActorRef
import akka.pattern.ask
import com.github.scalalab3.logs.services._

trait QueryServiceRoute extends AbstractHttpService {

  def queryService: ActorRef

  val queryRoute = get {
    (path("query") & parameter('query.?)) { string =>
      complete {
        pass(queryService ? Request(string))
      }
    }
  }
}