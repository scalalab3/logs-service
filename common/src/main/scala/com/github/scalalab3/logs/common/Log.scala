package com.github.scalalab3.logs.common

import java.time.OffsetDateTime
import java.util.UUID

case class Log(id: Option[UUID],
               level: Int,
               env: String,
               name: String,
               timestamp: OffsetDateTime,
               message: String,
               cause: String,
               stackTrace: String)
