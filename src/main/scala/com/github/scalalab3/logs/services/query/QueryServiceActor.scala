package com.github.scalalab3.logs.services.query

import akka.actor.{Actor, ActorContext, ActorRef}
import com.github.scalalab3.logs.components.QueryServiceComponentImpl

class QueryServiceActor(val dbService: ActorRef) extends Actor
  with QueryServiceRoute
  with QueryServiceComponentImpl {

  override def actorRefFactory: ActorContext = context
  override def receive: Receive = runRoute(queryRoute)
}
