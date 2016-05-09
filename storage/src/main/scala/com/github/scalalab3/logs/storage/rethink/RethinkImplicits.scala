package com.github.scalalab3.logs.storage.rethink

import java.util

import com.rethinkdb.RethinkDB
import com.rethinkdb.ast.ReqlAst
import com.rethinkdb.gen.ast.Db
import com.rethinkdb.net.Connection

object RethinkImplicits {

  implicit class RethinkDBExt(r: RethinkDB)(implicit c: Connection) {
    def dbSafe(name: String) = {
      val dbList: util.ArrayList[_] = r.dbList().run(c)
      if (!dbList.contains(name)) r.dbCreate(name).run(c)
      r.db(name)
    }
  }

  implicit class DbExt(db: Db)(implicit c: Connection) {
    def tableSafe(name: String) = {
      val tableList: util.ArrayList[_] = db.tableList().run(c)
      if (!tableList.contains(name)) db.tableCreate(name).run(c)
      db.table(name)
    }
  }

  implicit class ReqlAstExt(ast: ReqlAst)(implicit c: Connection) {
    def perform[A](): A = ast.run[A](c)
  }

}
