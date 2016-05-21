package com.github.scalalab3.logs.services

import akka.actor.ActorRef
import com.github.scalalab3.logs.common.Log

sealed trait AbstractResponse

case class BadRequest(error: String) extends AbstractResponse
case class LogsResponse(logs: Seq[Log]) extends AbstractResponse

case class Request(query: Option[String])
case class RequestStorage(req: Request, storage: ActorRef)