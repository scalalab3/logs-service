package com.github.scalalab3.logs.services.messages

import akka.actor.ActorRef
import com.github.scalalab3.logs.common.query.Query

case class QuerySender(query: Query, actorRef: ActorRef)
