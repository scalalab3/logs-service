package com.github.scalalab3.logs.services

import akka.actor._
import com.github.scalalab3.logs.services.query.QueryServiceRoute
import spray.routing._

import scala.concurrent.ExecutionContext

// QueryServiceRoute will be replaced with smth like:
// trait AppRoutes with QueryServiceRoute with AnalyticsServiceRoute with ReadRoute ... {
//   runRoute(queryRoute ~ analyticsRoute ~ readRoute ~ ...)
// }
class SystemActor extends HttpServiceActor with ActorLogging
   with QueryServiceRoute with ActorContextCreationSupport with StorageProvider {

  override def exc: ExecutionContext = context.dispatcher
  override val dbService = context.actorOf(Props(classOf[DummyStorageActor]), "db-actor")

  def receive = runRoute(queryRoute)
}
