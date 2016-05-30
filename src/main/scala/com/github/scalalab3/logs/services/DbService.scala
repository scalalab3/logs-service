package com.github.scalalab3.logs.services

import akka.actor.Actor
import com.github.scalalab3.logs.common.{Offset, OffsetBound, Slice}
import com.github.scalalab3.logs.storage.LogStorageComponent


class DbService(val component: LogStorageComponent) extends Actor {

  import Offset._

  val storage = component.logStorage

  def receive = {
    case RequestQuery(query) =>
      sender ! LogsResponse(storage.filter(query))
    case Create(log) =>
      storage.insert(log)
    case page: Page =>
      sender ! PageLogsResponse(storage.slice(page), storage.count())
    case GetChanges =>
      sender ! Changes(storage.changes())
  }

  implicit def psgeToSlice(page: Page): Slice = {
    val end = page.number * page.size
    val offset = OffsetBound(end - page.size, false) to OffsetBound(end, true)
    Slice(offset)
  }
}