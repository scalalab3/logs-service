package com.github.scalalab3.logs.services

import akka.actor.{ActorLogging, Actor}

trait AbstractService extends Actor with ActorLogging {
  override def unhandled(message: Any): Unit = {
    log.warning(s"Unhandled message $message")
    super.unhandled(message)
  }
}
