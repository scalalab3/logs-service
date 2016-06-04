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
        log.warning("=== resume")
        Resume
      case _: ReqlInternalError =>
        log.warning("=== resume")
        Resume
      case _: ReqlResourceLimitError =>
        log.warning("=== resume")
        Resume
      case _: ReqlPermissionError =>
        log.warning("=== stop")
        Stop
      case _: Exception =>
        log.warning("=== escalate")
        Escalate
    }

}
