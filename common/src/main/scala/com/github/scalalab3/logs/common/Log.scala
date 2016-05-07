package com.github.scalalab3.logs.common

import java.time.Instant
import java.util.UUID

case class Log(id: Option[UUID],
               level: Int,
               env: String,
               name: String,
               timestamp: Instant,
               message: String,
               cause: String,
               stackTrace: String)
