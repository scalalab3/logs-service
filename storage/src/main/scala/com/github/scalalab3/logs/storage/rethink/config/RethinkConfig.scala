package com.github.scalalab3.logs.storage.rethink.config

import com.github.scalalab3.logs.common.Environment
import com.github.scalalab3.logs.storage.rethink.config.RethinkKeys._
import com.typesafe.config.{Config, ConfigFactory}

case class RethinkConfig(host: String,
                         port: Int,
                         user: String,
                         password: String,
                         dbName: String,
                         tableName: String)

object RethinkConfig {
  private val config: Config = ConfigFactory.load()

  def apply(): RethinkConfig = load(config)

  def apply(env: Environment): RethinkConfig = load(config.getConfig(env.toKey).withFallback(config))

  private def load(config: Config): RethinkConfig = RethinkConfig(
    config.getString(host),
    config.getInt(port),
    config.getString(user),
    config.getString(password),
    config.getString(dbName),
    config.getString(tableName)
  )
}