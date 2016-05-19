package com.github.scalalab3.logs.storage.rethink

import com.github.scalalab3.logs.common.config.ConfigLoad
import com.github.scalalab3.logs.storage.rethink.config.DefaultValues._
import com.github.scalalab3.logs.storage.rethink.config.Keys._

case class RethinkConfig(host: String,
                         port: Int,
                         user: String,
                         password: String,
                         dbName: String,
                         tableName: String)

object RethinkConfig {
  private val config = new ConfigLoad()

  def load(): RethinkConfig =
    RethinkConfig(
      config.getString(host, defaultHost),
      config.getInt(port, defaultPort),
      config.getString(user, defaultUser),
      config.getString(password, defaultPassword),
      config.getString(dbName, defaultDbName),
      config.getString(tableName, defaultTableName)
    )

  val default = RethinkConfig(
    host = defaultHost,
    port = defaultPort,
    user = defaultUser,
    password = defaultPassword,
    dbName = defaultDbName,
    tableName = defaultTableName)
}