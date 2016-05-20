package com.github.scalalab3.logs.services

import akka.actor.{Actor, ActorLogging}

class SystemActor extends Actor with ActorLogging {
  def receive = {
    case msg => log.warning(s"Unhandled message: $msg")
  }
}