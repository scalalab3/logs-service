package com.github.scalalab3.logs.services

import akka.actor.Actor
import com.github.scalalab3.logs.services.messages.{LogsResponse, QuerySender}
import com.github.scalalab3.logs.tests.GenLog.randomLog

class DummyStorageActor extends Actor {
  override def receive: Receive = {
    case QuerySender(query, sender) => sender ! LogsResponse(List(randomLog(), randomLog(), randomLog()))
  }
}