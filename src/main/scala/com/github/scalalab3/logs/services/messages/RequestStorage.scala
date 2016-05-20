package com.github.scalalab3.logs.services.messages

import akka.actor.ActorRef

case class RequestStorage(req: Request, actorRef: ActorRef)