package com.github.scalalab3.logs.storage.rethink

import java.util

import com.github.scalalab3.logs.common_macro._
import com.rethinkdb.RethinkDB
import com.rethinkdb.ast.ReqlAst
import com.rethinkdb.gen.ast.{Table, Db}
import com.rethinkdb.net.{Cursor, Connection}
import shapeless.Typeable

object RethinkImplicits {

  implicit class RethinkDBExt(r: RethinkDB)(implicit c: Connection) {
    def dbSafe(name: String): Option[Db] = {
      for {
        dbList <- Typeable[util.List[_]].cast(r.dbList().run(c))
      } yield {
        if (!dbList.contains(name)) r.dbCreate(name).run(c)
        r.db(name)
      }
    }
  }

  implicit class DbExt(db: Db)(implicit c: Connection) {
    def tableSafe(name: String): Option[Table] = {
      for {
        tableList <- Typeable[util.List[_]].cast(db.tableList().run(c))
      } yield {
        if (!tableList.contains(name)) db.tableCreate(name).run(c)
        db.table(name)
      }
    }
  }

  implicit class ReqlAstExt(ast: ReqlAst)(implicit c: Connection) {
    def perform[A](): A = ast.run[A](c)
  }

  implicit val typeableCursor: Typeable[Cursor[HM]] =
    new Typeable[Cursor[HM]] {
      override def cast(t: Any): Option[Cursor[HM]] = {
        if (t == null) None
        else t match {
          case c: Cursor[HM] => Some(c)
          case _ => None
        }
      }

      override def describe: String = "Cursor[HM]"
    }

  implicit val typeableList: Typeable[util.List[_]] =
    new Typeable[util.List[_]] {
      override def cast(t: Any): Option[util.List[_]] = {
        if (t == null) None
        else t match {
          case c: util.List[_] => Some(c)
          case _ => None
        }
      }

      override def describe: String = "util.ArrayList[_]"
    }
}
