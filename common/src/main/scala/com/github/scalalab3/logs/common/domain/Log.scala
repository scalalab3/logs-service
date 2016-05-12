package com.github.scalalab3.logs.common.domain

import java.time.OffsetDateTime
import java.util.UUID

case class Log(id: Option[UUID],
               level: String,
               env: String,
               name: String,
               dateTime: OffsetDateTime,
               message: String,
               cause: String,
               stackTrace: String)
