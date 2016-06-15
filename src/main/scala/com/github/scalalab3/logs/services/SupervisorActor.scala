package com.github.scalalab3.logs.services

import akka.actor.ActorRef


class SupervisorActor(readService: ActorRef, queryService: ActorRef) extends AbstractActor {

  def receive = {
    case page: Page =>
      readService ! page
    case request: Request =>
      queryService ! request
  }

}
