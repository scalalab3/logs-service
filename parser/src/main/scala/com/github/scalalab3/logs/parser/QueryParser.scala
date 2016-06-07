package com.github.scalalab3.logs.parser

import com.github.scalalab3.logs.common.query.Query

import scalaz.\/

trait QueryParser {
  def parse(query: String): String \/ Query
}