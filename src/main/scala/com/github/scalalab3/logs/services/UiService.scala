package com.github.scalalab3.logs.services
import akka.actor.{ActorRef, ActorRefFactory}
import akka.event.LoggingReceive
import com.github.scalalab3.logs.http.{QueryServiceRoute, ReadServiceRoute}
import spray.http.StatusCodes
import spray.routing.{ExceptionHandler, HttpService}


class UiService(service: ActorRef) extends AbstractActor with HttpService with QueryServiceRoute with ReadServiceRoute {

  val exceptionHandler = ExceptionHandler {
    case _: Exception => complete(StatusCodes.InternalServerError)
  }

  override def receive = LoggingReceive {
    runRoute(
      handleExceptions(exceptionHandler) {
        queryRoute ~
        readRoute
      }
    )
  }

  override def abstractService: ActorRef = service

  override def actorRefFactory: ActorRefFactory = context
}
