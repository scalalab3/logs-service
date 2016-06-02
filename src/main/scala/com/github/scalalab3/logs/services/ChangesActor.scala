package com.github.scalalab3.logs.services

import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.actor.{ActorRef, OneForOneStrategy}
import com.rethinkdb.gen.exc.ReqlInternalError

class ChangesActor(dbService: ActorRef) extends AbstractActor {

  implicit val system = context.system
  val stream = system.eventStream

  override def preStart = {
    dbService ! GetChanges()
  }

  override def receive = {
    case Changes(iterator) =>
      iterator
        .map(LogChange)
        .foreach(stream.publish)
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _: ReqlInternalError =>
        println("=== resume")
        Resume
      case _: Exception     =>
        println("=== restart")
        Restart
    }
}