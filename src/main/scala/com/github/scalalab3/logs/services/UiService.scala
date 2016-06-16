package com.github.scalalab3.logs.services
import akka.actor.{ActorRef, ActorRefFactory}
import akka.event.LoggingReceive
import com.github.scalalab3.logs.http.{QueryServiceRoute, ReadServiceRoute}
import spray.http.HttpHeaders.RawHeader
import spray.http.{MediaTypes, StatusCodes}
import spray.routing.{ExceptionHandler, HttpService}


class UiService(override val abstractService: ActorRef) extends AbstractActor with HttpService with QueryServiceRoute with ReadServiceRoute {

  val exceptionHandler = ExceptionHandler {
    case _: Exception => complete(StatusCodes.InternalServerError)
  }

  val fileName = "index.html"

  val root = pathEndOrSingleSlash {
    respondWithMediaType(MediaTypes.`text/html`) {
      complete(scala.io.Source.fromInputStream(
        getClass.getResourceAsStream(s"/$fileName")).mkString)
    }
  }


  override def receive = LoggingReceive {
    runRoute(
      handleExceptions(exceptionHandler) {
        respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
          root ~
            queryRoute ~
            readRoute
        }
      }
    )
  }



  override def actorRefFactory: ActorRefFactory = context
}
