package com.github.scalalab3.logs.services.query

import akka.actor.{ActorRef, ActorRefFactory, Props}
import akka.testkit.{TestActor, TestProbe}
import com.github.scalalab3.logs.services.messages.{LogsResponse, RequestStorage}
import com.github.scalalab3.logs.services.{ActorCreationSupport, StorageProvider}
import com.github.scalalab3.logs.tests.{GenLog, LogJsonSpecification, Specs2RouteTest}
import org.specs2.specification.AfterAll
import spray.http.{HttpMethods, StatusCodes, Uri}
import spray.routing.MethodRejection

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class QueryServiceTest extends LogJsonSpecification with AfterAll with Specs2RouteTest {

  implicit val routeTestTimeout = RouteTestTimeout(5.second)

  val queryService = TestProbe()
  queryService.setAutoPilot {
    new TestActor.AutoPilot {
      def run(sender: ActorRef, msg: Any) = msg match {
        case RequestStorage(_, _) => sender ! LogsResponse(Seq(log))
          TestActor.KeepRunning
        case _ => TestActor.NoAutoPilot
      }
    }
  }

  val subject = new QueryServiceRoute with StorageProvider with ActorCreationSupport {
    override implicit def exc: ExecutionContext = system.dispatcher
    override implicit def actorRefFactory: ActorRefFactory = system
    override def dbService: ActorRef = TestProbe().ref
    override def create(props: Props, name: String): ActorRef = queryService.ref
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
      Put(Uri("/query").withQuery(Map("query" -> "name = 'log'"))) ~> queryRoute ~> check {
        rejection === MethodRejection(HttpMethods.GET)
      }
      Post(Uri("/query").withQuery(Map("query" -> "name != 'log'"))) ~> queryRoute ~> check {
        rejection === MethodRejection(HttpMethods.GET)
      }
    }
  }
}