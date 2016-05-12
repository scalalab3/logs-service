package com.github.scalalab3.logs.storage.rethink

import com.typesafe.config.ConfigFactory

import scala.util.Try

case class RethinkConfig(host: String = "localhost",
                         port: Int = 28015,
                         user: String = "admin",
                         password: String = "",
                         dbName: String = "test",
                         tableName: String = "test")

object RethinkConfig {
  def load(): RethinkConfig = {
    val config = ConfigFactory.load()

    RethinkConfig(
      Try(config.getString("rethink.host")).getOrElse("localhost"),
      Try(config.getInt("rethink.port")).getOrElse(28015),
      Try(config.getString("rethink.user")).getOrElse("admin"),
      Try(config.getString("rethink.password")).getOrElse(""),
      Try(config.getString("rethink.db.name")).getOrElse("test"),
      Try(config.getString("rethink.table.name")).getOrElse("test")
    )
  }
}