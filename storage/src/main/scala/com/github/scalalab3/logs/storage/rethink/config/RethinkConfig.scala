package com.github.scalalab3.logs.storage.rethink.config

import com.github.scalalab3.logs.storage.rethink.config.RethinkKeys._
import com.typesafe.config.ConfigFactory

case class RethinkConfig(host: String,
                         port: Int,
                         user: String,
                         password: String,
                         dbName: String,
                         tableName: String)

object RethinkConfig {
  private val config = ConfigFactory.load()

  def load(): RethinkConfig = RethinkConfig(
    config.getString(host),
    config.getInt(port),
    config.getString(user),
    config.getString(password),
    config.getString(dbName),
    config.getString(tableName)
  )
}