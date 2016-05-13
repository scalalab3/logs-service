package com.github.scalalab3.logs.storage.rethink

import com.github.scalalab3.logs.storage.rethink.constant.PropertyDefaultValues._
import com.github.scalalab3.logs.storage.rethink.constant.PropertyKeys._
import com.typesafe.config.ConfigFactory

import scala.util.Try

case class RethinkConfig(host: String = defaultHost,
                         port: Int = defaultPort,
                         user: String = defaultUser,
                         password: String = defaultPassword,
                         dbName: String = defaultDbName,
                         tableName: String = defaultTableName)

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

  private def getString(key: String, defaultValue: String) = Try(config.getString(key)).getOrElse(defaultValue)
  private def getInt(key: String, defaultValue: Int) = Try(config.getInt(key)).getOrElse(defaultValue)
}