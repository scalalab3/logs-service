package com.github.scalalab3.logs.services.query

import akka.actor.{Actor, ActorContext, ActorRef}

class QueryServiceActor(val dbService: ActorRef) extends Actor with QueryServiceRoute {

  override def actorRefFactory: ActorContext = context
  override def receive: Receive = runRoute(queryRoute)
}
