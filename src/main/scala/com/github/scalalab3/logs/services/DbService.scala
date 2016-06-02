package com.github.scalalab3.logs.services

import akka.actor.SupervisorStrategy.{Escalate, Resume, Stop}
import akka.actor.{Actor, OneForOneStrategy}
import com.github.scalalab3.logs.common.Slice
import com.github.scalalab3.logs.storage.LogStorageComponent
import com.rethinkdb.gen.exc.{ReqlPermissionError, ReqlResourceLimitError}

class DbService(val component: LogStorageComponent) extends Actor {

  val storage = component.logStorage

  def receive = {
    case RequestQuery(query) =>
      sender ! LogsResponse(storage.filter(query))
    case Create(log) =>
      storage.insert(log)
    case slice: Slice =>
      sender ! PageLogsResponse(storage.slice(slice), storage.count())
    case GetChanges =>
      sender ! Changes(storage.changes())
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _: ReqlResourceLimitError =>
        println("=== resume")
        Resume
      case _: ReqlPermissionError =>
        println("=== stop")
        Stop
      case _: Exception     =>
        println("=== escalate")
        Escalate
    }
}