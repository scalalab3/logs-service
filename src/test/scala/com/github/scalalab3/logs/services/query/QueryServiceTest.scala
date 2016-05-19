package com.github.scalalab3.logs.services.query

import akka.actor.ActorRef
import akka.testkit.{TestActor, TestProbe}
import com.github.scalalab3.logs.common.query.Query
import com.github.scalalab3.logs.components.QueryServiceComponentImpl
import com.github.scalalab3.logs.tests.{GenLog, LogJsonSpecification, Specs2RouteTest}
import org.specs2.specification.AfterAll
import spray.http.{StatusCodes, Uri}

class QueryServiceTest extends LogJsonSpecification with AfterAll
  with Specs2RouteTest
  with QueryServiceRoute
  with QueryServiceComponentImpl {

  def actorRefFactory = system

  val log = GenLog.randomLog()

  val probe = TestProbe()
  probe.setAutoPilot {
    new TestActor.AutoPilot {
      def run(sender: ActorRef, msg: Any) = msg match {
        case q: Query =>
          sender ! List(log)
          TestActor.KeepRunning
        case _ => TestActor.NoAutoPilot
      }
    }
  }

  "QueryService should" >> {
    "return Status OK for GET requests" in {
      Get(Uri("/query").withQuery(Map("query" -> "name contains 'log'"))) ~> queryRoute ~> check {
        status === StatusCodes.OK
      }
      Get(Uri("/query").withQuery(Map("query" -> "level = 'info'"))) ~> queryRoute ~> check {
        status === StatusCodes.OK
      }
    }

    "return logs in json format" in {
      Get(Uri("/query").withQuery(Map("query" -> "999 min"))) ~> queryRoute ~> check {
        responseAs[String] must haveLogs(aLogWith(name = log.name, id = log.id.get.toString, dateTime = log.dateTime.toString))
      }
    }

    "return Status BadRequest for GET requests" in {
      Get(Uri("/query").withQuery(Map("query" -> "name <> 'log'"))) ~> queryRoute ~> check {
        status === StatusCodes.BadRequest
      }
      Get(Uri("/query").withQuery(Map("get" -> "name = 'log'"))) ~> queryRoute ~> check {
        status === StatusCodes.BadRequest
      }
      Get(Uri("/query")) ~> queryRoute ~> check {
        status === StatusCodes.BadRequest
      }
    }
  }

  override def dbService: ActorRef = probe.ref

  override def afterAll(): Unit = {
    dbService ! None
    system.terminate()
  }
}


