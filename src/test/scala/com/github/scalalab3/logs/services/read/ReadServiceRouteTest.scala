package com.github.scalalab3.logs.services.read

import akka.actor.{Actor, ActorRef, ActorRefFactory}
import akka.testkit.TestActorRef
import com.github.scalalab3.logs.services.{LogsResponse, Page}
import com.github.scalalab3.logs.tests.GenLog.randomLog
import com.github.scalalab3.logs.tests.Specs2RouteTest
import org.specs2.mutable.Specification
import spray.http.HttpHeaders.RawHeader
import spray.http.{HttpMethods, StatusCodes, Uri}
import spray.routing.MethodRejection

class ReadServiceRouteTest extends Specification with Specs2RouteTest {

  val readTestService = TestActorRef(new Actor {
    def receive = {
      case Page(_, _) => sender ! LogsResponse(Seq(randomLog(), randomLog(), randomLog()))
    }
  })

  val subject = new ReadServiceRoute {
    override implicit def actorRefFactory: ActorRefFactory = system
    override def readService: ActorRef = readTestService
  }

  val readRoute = subject.readRoute

  "ReadService should" >> {
    "return Status OK for GET requests" in {
      Get(Uri("/")) ~> readRoute ~> check {
        status === StatusCodes.OK
      }
      Get(Uri("/").withQuery("page" -> "2", "per_page" -> "5")) ~> readRoute ~> check {
        status === StatusCodes.OK
      }
      Get(Uri("/").withQuery("per_page" -> "30", "page" -> "1")) ~> readRoute ~> check {
        status === StatusCodes.OK
      }
      Get(Uri("/").withQuery("per_page" -> "50")) ~> readRoute ~> check {
        status === StatusCodes.OK
      }
      Get(Uri("/").withQuery("page" -> "3")) ~> readRoute ~> check {
        status === StatusCodes.OK
      }
    }

    "return header 'X-Total' with size" in {
      Get(Uri("/")) ~> readRoute ~> check {
        status === StatusCodes.OK
        header("X-Total") === Some(RawHeader("X-Total", "3"))
      }
    }

    "not handled GET requests for another uri" in {
      Get(Uri("/data")) ~> readRoute ~> check {
        handled === false
      }
    }

    "reject another methods" in {
      Put(Uri("/")) ~> readRoute ~> check {
        rejection === MethodRejection(HttpMethods.GET)
      }
      Post(Uri("/")) ~> readRoute ~> check {
        rejection === MethodRejection(HttpMethods.GET)
      }
    }
  }
}
