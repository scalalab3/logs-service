package com.github.scalalab3.logs.http

import akka.pattern.ask
import com.github.scalalab3.logs.services._

trait ReadServiceRoute extends AbstractHttpService {

  val readRoute = get {
    (pathSingleSlash & parameter('page.as[Int] ? 1) & parameter('per_page.as[Int] ? 100)) { (num, size) =>
      validate(num > 0 && size > 0, "The number and size of the page must be greater than 0.") {
        complete {
          pass(abstractService ? Page(num, size))
        }
      }
    }
  }
}
