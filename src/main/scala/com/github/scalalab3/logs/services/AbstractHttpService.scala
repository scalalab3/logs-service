package com.github.scalalab3.logs.services

import akka.util.Timeout
import spray.routing.HttpService
import scala.concurrent.duration._

trait AbstractHttpService extends HttpService {

  implicit val timeout = Timeout(5.seconds)
  implicit def executionContext = actorRefFactory.dispatcher

}
