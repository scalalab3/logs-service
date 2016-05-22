package com.github.scalalab3.logs.services

import com.github.scalalab3.logs.storage.rethink.config.RethinkConfig
import com.typesafe.config.ConfigFactory


object TestRethinkConfig {
  private val config = ConfigFactory.load()

  def load(): RethinkConfig = RethinkConfig(
    "46.101.108.45",
    28015,
    "admin",
    "",
    "test",
    "logs"
  )
}
