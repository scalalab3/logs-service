package com.github.scalalab3.logs.storage.rethink

import com.github.scalalab3.logs.storage.rethink.RethinkImplicits._
import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import com.typesafe.config.{Config, ConfigFactory}

object RethinkContext {

  private val config: Config = ConfigFactory.load()

  val r = RethinkDB.r
  implicit val connect: Connection = r.connection
    .hostname(config.getString("rethink.host"))
    .port(config.getInt("rethink.port"))
    .connect

  val dbName = config.getString("rethink.db.name")
  val tableName = config.getString("rethink.table.name")

  def table = r.dbSafe(dbName).tableSafe(tableName)

  def dropWork(): Unit = {
    r.dbSafe(dbName).tableDrop(tableName).perform()
    r.dbDrop(dbName).perform()
  }
}