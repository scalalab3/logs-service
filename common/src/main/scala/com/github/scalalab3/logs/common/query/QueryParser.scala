package com.github.scalalab3.logs.common.query

import scala.util.Try

trait QueryParser {
  def parse(query: String): Try[Query]
}
