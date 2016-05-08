package com.github.scalalab3.logs.parser

import com.github.scalalab3.logs.query._

import scala.util.{Failure, Success, Try}

class StubParser extends QueryParser {

  // feel free to add your mappings here
  val possibleQueries = Map(
    "field = 'value'" -> Eq("field", "value"),
    "env = 'production'" -> Eq("env", "production"),
    "env = 'production' AND message contains 'Failed to'" -> And(Eq("env", "production"), Contains("message", "Failed to"))
  )

  override def parse(query: String): Try[Query] = possibleQueries.get(query)
    .map(Success(_)) getOrElse Failure(new RuntimeException("Wrong query"))
}
