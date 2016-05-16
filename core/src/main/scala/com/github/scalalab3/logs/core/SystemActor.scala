package com.github.scalalab3.logs.core

import akka.actor.{Actor, ActorLogging}


class SystemActor extends Actor with ActorLogging {
  def receive = {
    case msg => {
      println(s"[SystemActor] received: $msg")
      log.debug(s"received: $msg")
    }
  }
  println("Launch SystemActor")
}
