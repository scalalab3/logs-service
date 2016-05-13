package com.github.scalalab3.logs.common

import java.time.OffsetDateTime
import java.util.UUID

case class Log(id: Option[UUID],
               level: Level,
               env: String,
               name: String,
               dateTime: OffsetDateTime,
               message: String,
               cause: String,
               stackTrace: String)