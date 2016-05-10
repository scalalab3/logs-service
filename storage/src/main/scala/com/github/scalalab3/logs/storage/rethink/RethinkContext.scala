package com.github.scalalab3.logs.storage.rethink

import com.github.scalalab3.logs.storage.rethink.RethinkImplicits._
import com.rethinkdb.RethinkDB
import com.rethinkdb.gen.ast.Table
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

  def table: Option[Table] = r.dbSafe(dbName).flatMap(_.tableSafe(tableName))

  def dropWork(): Unit = for (db <- r.dbSafe(dbName)) {
    db.tableDrop(tableName).perform()
    r.dbDrop(dbName).perform()
  }

}