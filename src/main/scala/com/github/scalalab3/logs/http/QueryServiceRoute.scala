package com.github.scalalab3.logs.http

import akka.pattern.ask
import com.github.scalalab3.logs.services._

trait QueryServiceRoute extends AbstractHttpService {

  val queryRoute = get {
    (path("query") & parameter('query.?)) { string =>
      complete {
        pass(abstractService ? Request(string))
      }
    }
  }
}