package com.github.scalalab3.logs.storage.rethink

import com.github.scalalab3.logs.storage.rethink.constants.PropertyDefaultValues._
import com.github.scalalab3.logs.storage.rethink.constants.PropertyKeys._
import com.typesafe.config.ConfigFactory

import scala.util.Try

case class RethinkConfig(host: String,
                         port: Int,
                         user: String,
                         password: String,
                         dbName: String,
                         tableName: String)

object RethinkConfig {

  private val config = ConfigFactory.load()

  def load(): RethinkConfig =
    RethinkConfig(
      getString(host, defaultHost),
      getInt(port, defaultPort),
      getString(user, defaultUser),
      getString(password, defaultPassword),
      getString(dbName, defaultDbName),
      getString(tableName, defaultTableName)
    )

  val default = RethinkConfig(
    host = defaultHost,
    port = defaultPort,
    user = defaultUser,
    password = defaultPassword,
    dbName = defaultDbName,
    tableName = defaultTableName)

  private def getString(key: String, defaultValue: String) = Try(config.getString(key)).getOrElse(defaultValue)

  private def getInt(key: String, defaultValue: Int) = Try(config.getInt(key)).getOrElse(defaultValue)
}