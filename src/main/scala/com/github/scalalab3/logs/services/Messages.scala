package com.github.scalalab3.logs.services

import com.github.scalalab3.logs.common.Log

sealed trait AbstractResponse

case class BadRequest(error: String) extends AbstractResponse
case class LogsResponse(logs: Seq[Log]) extends AbstractResponse
case class PageLogsResponse(logs: Seq[Log]) extends AbstractResponse

case class Request(query: Option[String])
case class Page(number: Int, size: Int)

case object Ready
