package com.github.scalalab3.logs.services

import akka.actor.{ActorRef, Props}


class SupervisorActor(dbService: ActorRef, dn: String) extends AbstractActor {

  val queryService = context.actorOf(Props(classOf[QueryServiceActor], dbService).withDispatcher(dn), "query-actor")
  val readService = context.actorOf(Props(classOf[ReadServiceActor], dbService).withDispatcher(dn), "read-actor")

  def receive = {
    case page: Page =>
      readService forward page
    case request: Request =>
      queryService forward request
  }

}
