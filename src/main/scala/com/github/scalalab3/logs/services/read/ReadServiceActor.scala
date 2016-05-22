package com.github.scalalab3.logs.services.read

import akka.actor.{Actor, ActorRef}
import com.github.scalalab3.logs.common.Offset._
import com.github.scalalab3.logs.common._
import com.github.scalalab3.logs.services.{BadRequest, Page}

class ReadServiceActor(dbService: ActorRef) extends Actor {

  override def receive: Receive = {
    case page @ Page(num, size) if num > 0 && size > 0 => dbService forward Slice(offset = bounds(page))
    case _ => sender ! BadRequest("Bad request")
  }

  def bounds(page: Page): Offset = {
    val start = (page.number - 1) * page.size
    val end = start + page.size

    OffsetBound(start, isClosed = true) to OffsetBound(end, isClosed = false)
  }
}
