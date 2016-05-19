package com.github.scalalab3.logs.storage.rethink

import com.github.scalalab3.logs.storage.rethink.RethinkImplicits._
import com.rethinkdb.RethinkDB
import com.rethinkdb.gen.ast.{Db, Table}
import com.rethinkdb.net.Connection

class RethinkContext(val config: RethinkConfig) {

  val rethinkDb = RethinkDB.r
  implicit val connect: Connection = rethinkDb.connection
    .hostname(config.host)
    .port(config.port)
    .user(config.user, config.password)
    .connect

  def db(dbName: String = config.dbName): Db = rethinkDb.dbSafe(dbName)

  def dbDrop(dbName: String = config.dbName): Unit = rethinkDb.dbDropSafe(dbName)

  def table(dbName: String = config.dbName,
            tableName: String = config.tableName): Table = rethinkDb.dbSafe(dbName).tableSafe(tableName)

  def tableDrop(dbName: String = config.dbName,
                tableName: String = config.tableName): Unit = rethinkDb.dbSafe(dbName).tableDropSafe(tableName)
}