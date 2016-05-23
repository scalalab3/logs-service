package com.github.scalalab3.logs.services

import akka.actor.{Actor, ActorLogging}

trait AbstractActor extends Actor with ActorLogging {

  override def unhandled(message: Any) = log.warning(s"Unexpected message $message")

}
