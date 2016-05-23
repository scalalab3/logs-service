package com.github.scalalab3.logs.services.http

import akka.actor.{Actor, ActorRef, ActorRefFactory}
import akka.testkit.TestActorRef
import com.github.scalalab3.logs.http.ReadServiceRoute
import com.github.scalalab3.logs.services.{Page, PageLogsResponse}
import com.github.scalalab3.logs.tests.GenLog.randomLog
import com.github.scalalab3.logs.tests.Specs2RouteTest
import org.specs2.mutable.Specification
import spray.http.HttpHeaders.RawHeader
import spray.http.{HttpMethods, StatusCodes, Uri}
import spray.routing.{ValidationRejection, MethodRejection}

class ReadServiceRouteTest extends Specification with Specs2RouteTest {

  val readTestService = TestActorRef(new Actor {
    def receive = {
      case Page(_, _) => sender ! PageLogsResponse(Seq(randomLog(), randomLog(), randomLog()))
    }
  })

  val subject = new ReadServiceRoute {
    override implicit def actorRefFactory: ActorRefFactory = system
    override def abstractService: ActorRef = readTestService
  }

  val readRoute = subject.readRoute

  "ReadService should" >> {
    "return Status OK for GET valid requests" in {
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
    }

    "reject invalid requests" in {
      Get(Uri("/").withQuery("page" -> "-1", "per_page" -> "0")) ~> readRoute ~> check {
        rejection === ValidationRejection("The number and size of the page must be greater than 0.", None)
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
