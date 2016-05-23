package com.github.scalalab3.logs.http

import akka.actor.ActorRef
import akka.pattern.ask
import com.github.scalalab3.logs.services._

trait ReadServiceRoute extends AbstractHttpService {

  def readService: ActorRef

  val readRoute = get {
    (pathSingleSlash & parameter('page.as[Int] ? 1) & parameter('per_page.as[Int] ? 100)) { (num, size) =>
      complete {
        pass(readService ? Page(num, size))
      }
    }
  }
}
