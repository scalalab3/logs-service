package com.github.scalalab3.logs.services

import java.nio.channels.NotYetConnectedException

import akka.actor.SupervisorStrategy.{Escalate, Resume, Stop}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy}
import com.rethinkdb.gen.exc.{ReqlInternalError, ReqlPermissionError, ReqlResourceLimitError}

trait AbstractActor extends Actor with ActorLogging {

  override def unhandled(message: Any) = log.warning(s"Unexpected message $message")

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _: NotYetConnectedException =>
        println("=== resume")
        Resume
      case _: ReqlInternalError =>
        println("=== resume")
        Resume
      case _: ReqlResourceLimitError =>
        println("=== resume")
        Resume
      case _: ReqlPermissionError =>
        println("=== stop")
        Stop
      case _: Exception =>
        println("=== escalate")
        Escalate
    }

}
