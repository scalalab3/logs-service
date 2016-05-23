package com.github.scalalab3.logs.services.query

import akka.actor.{Actor, ActorRef, ActorRefFactory}
import akka.testkit.TestActorRef
import com.github.scalalab3.logs.http.QueryServiceRoute
import com.github.scalalab3.logs.services.{LogsResponse, Request}
import com.github.scalalab3.logs.tests.{GenLog, LogJsonSpecification, Specs2RouteTest}
import spray.http.{HttpMethods, StatusCodes, Uri}
import spray.routing.MethodRejection

class QueryServiceTest extends LogJsonSpecification with Specs2RouteTest {

  val queryTestService = TestActorRef(new Actor {
    def receive = {
      case Request(_) => sender ! LogsResponse(Seq(log))
    }
  })

  val subject = new QueryServiceRoute {
    override implicit def actorRefFactory: ActorRefFactory = system
    override def queryService: ActorRef = queryTestService
  }

  val queryRoute = subject.queryRoute
  val log = GenLog.randomLog()

  "QueryService should" >> {
    "return Status OK for GET requests" in {
      Get(Uri("/query").withQuery("query" -> "name contains 'log'")) ~> queryRoute ~> check {
        status === StatusCodes.OK
      }
      Get(Uri("/query").withQuery("query" -> "level = 'info'")) ~> queryRoute ~> check {
        status === StatusCodes.OK
      }
    }

    "return logs in json format" in {
      Get(Uri("/query").withQuery("query" -> "999 min")) ~> queryRoute ~> check {
        responseAs[String] must haveLogs(aLogWith(name = log.name, id = log.id.get.toString, level = log.level.toString))
      }
    }

    "not handled GET requests for another uri" in {
      Get(Uri("/sql").withQuery("query" -> "name = 'log'")) ~> queryRoute ~> check {
        handled === false
      }
      Get(Uri("/")) ~> queryRoute ~> check {
        handled === false
      }
    }

    "reject another methods" in {
      Put(Uri("/query").withQuery("query" -> "name = 'log'")) ~> queryRoute ~> check {
        rejection === MethodRejection(HttpMethods.GET)
      }
      Post(Uri("/query").withQuery("query" -> "name != 'log'")) ~> queryRoute ~> check {
        rejection === MethodRejection(HttpMethods.GET)
      }
    }
  }
}