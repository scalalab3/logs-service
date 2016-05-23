package com.github.scalalab3.logs.services

import com.github.scalalab3.logs.storage.rethink.config.RethinkConfig

object TestRethinkConfig {

  def load(): RethinkConfig = RethinkConfig(
    "localhost",
    28015,
    "admin",
    "",
    "test",
    "logs"
  )
}
