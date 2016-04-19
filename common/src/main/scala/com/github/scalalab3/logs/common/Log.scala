package com.github.scallab3.logs.common

import java.time.{Instant}
import java.util.{UUID}


case class Log(
  id: Option[UUID],
  level: Integer,
  env: String,
  name: String,
  timestamp: Instant,
  message: String,
  cause: String,
  stackTrace: String
)
