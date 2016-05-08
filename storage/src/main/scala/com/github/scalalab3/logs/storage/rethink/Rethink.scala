package com.github.scalalab3.logs.storage.rethink

import java.util

import com.rethinkdb.RethinkDB
import com.rethinkdb.ast.ReqlAst
import com.rethinkdb.net.Connection
import com.typesafe.config.{Config, ConfigFactory}

object Rethink {

  private val config: Config = ConfigFactory.load()

  val r = RethinkDB.r
  val connect: Connection = r.connection
    .hostname(config.getString("rethink.host"))
    .port(config.getInt("rethink.port"))
    .connect

  implicit class ReqlAstExt(ast: ReqlAst) {
    def run[A](): A = ast.run[A](connect)
  }

  val dbName = config.getString("rethink.db.name")
  if (!isExistsDB(dbName)) r.dbCreate(dbName).run()
  connect.use(dbName)

  val tableName = config.getString("rethink.table.name")
  if (!isExistsTable(tableName)) r.tableCreate(tableName).run()

  def table = r.table(tableName)

  private def isExistsDB(dbName: String): Boolean = {
    val dbList = r.dbList().run().asInstanceOf[util.ArrayList[_]]
    dbList.contains(dbName)
  }

  private def isExistsTable(tableName: String): Boolean = {
    val tableList = r.tableList().run().asInstanceOf[util.ArrayList[_]]
    tableList.contains(tableName)
  }

  def dropWork(): Unit = {
    r.tableDrop(tableName).run()
    r.dbDrop(dbName).run()
    connect.close()
  }
}