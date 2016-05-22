package com.github.scalalab3.logs.services.read

import akka.actor.{Actor, ActorRef}
import com.github.scalalab3.logs.common.offset.Offset._
import com.github.scalalab3.logs.common.offset.{Offset, OffsetBound, Slice}
import com.github.scalalab3.logs.services.Page

class ReadServiceActor(dbService: ActorRef) extends Actor {

  override def receive: Receive = {
    case page @ Page(_, _) => dbService forward Slice(offset = bounds(page))
  }

  def bounds(page: Page): Offset = {
    val start = (page.number - 1) * page.size
    val end = start + page.size

    OffsetBound(start, isClosed = true) to OffsetBound(end, isClosed = false)
  }
}
