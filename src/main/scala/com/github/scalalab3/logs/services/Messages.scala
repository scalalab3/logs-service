package com.github.scalalab3.logs.services

import com.github.scalalab3.logs.common.Log
import com.github.scalalab3.logs.common.query.Query

sealed trait AbstractResponse

case class BadRequest(error: String) extends AbstractResponse
case class LogsResponse(logs: Seq[Log]) extends AbstractResponse
case class PageLogsResponse(logs: Seq[Log], count: Long) extends AbstractResponse
case class Changes(logs: Iterator[Log]) extends AbstractResponse

sealed trait AbstractRequest

case class Request(query: Option[String]) extends AbstractRequest
case class RequestQuery(query: Query) extends AbstractRequest
case class Page(number: Int, size: Int) extends AbstractRequest
case class Create(log: Log) extends AbstractRequest
case object GetChanges extends AbstractRequest

case class LogChange(log: Log)

case object Ready
