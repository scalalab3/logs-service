package com.github.scalalab3.logs.services

import akka.actor.ActorRef

class ChangesActor(dbService: ActorRef) extends AbstractActor {

  implicit val system = context.system
  val stream = system.eventStream

  dbService ! GetChanges()

  override def receive = {
    case Changes(iterator) =>
      for (log <- iterator) {
        stream.publish(LogChange(log))
      }
  }
}