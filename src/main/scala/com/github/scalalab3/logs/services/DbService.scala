package com.github.scalalab3.logs.services

import akka.actor.Actor
import com.github.scalalab3.logs.common.Slice
import com.github.scalalab3.logs.storage.LogStorageComponent

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

}