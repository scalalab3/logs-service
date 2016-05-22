package com.github.scalalab3.logs.services

import com.github.scalalab3.logs.storage.rethink.config.RethinkConfig
import com.typesafe.config.ConfigFactory


object TestRethinkConfig {
  private val config = ConfigFactory.load()

  def load(): RethinkConfig = RethinkConfig(
    "localhost",
    28015,
    "admin",
    "",
    "test",
    "logs"
  )
}
