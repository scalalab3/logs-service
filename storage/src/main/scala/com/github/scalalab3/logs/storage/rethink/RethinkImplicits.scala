package com.github.scalalab3.logs.storage.rethink

import java.util

import com.github.scalalab3.logs.common_macro._
import com.rethinkdb.RethinkDB
import com.rethinkdb.ast.ReqlAst
import com.rethinkdb.gen.ast.{Db, ReqlFunction1, Table}
import com.rethinkdb.net.{Connection, Cursor}
import shapeless.Typeable

import scala.util.Try

object RethinkImplicits {

  implicit class ReqlAstExt(ast: ReqlAst)(implicit c: Connection) {
    @inline def perform[A](): A = ast.run[A](c)
  }

  private def ifExists(name: String, optList: Option[util.List[_]]): Boolean = {
    val res = for {
      str <- Option(name)
      list <- optList
    } yield list.contains(str)

    res.getOrElse(false)
  }

  implicit class RethinkDBExt(r: RethinkDB)(implicit c: Connection) {

    def dbList = Typeable[util.List[_]].cast(r.dbList().perform())

    def dbSafe(name: String): Option[Db] = {
      if (!ifExists(name, dbList)) r.dbCreate(name).perform()
      Option(r.db(name))
    }

    def dbDropSafe(name: String): Unit = {
      if (ifExists(name, dbList)) r.dbDrop(name).perform()
    }
  }

  implicit class DbExt(db: Db)(implicit c: Connection) {

    def tableList = Typeable[util.List[_]].cast(db.tableList().perform())

    def tableSafe(name: String): Option[Table] = {
      if (!ifExists(name, tableList)) db.tableCreate(name).perform()
      Option(db.table(name))
    }

    def tableDropSafe(name: String): Unit = {
      if (ifExists(name, tableList)) db.tableDrop(name).perform()
    }
  }

  implicit class TableExt(table: Table)(implicit c: Connection) {
    def cursorSafe(): Option[Cursor[HM]] = Typeable[Cursor[HM]].cast(table.perform())

    def filterSafe(func1: ReqlFunction1): Option[Cursor[HM]] = Try {
      Typeable[Cursor[HM]].cast {
        table.filter(func1).perform()
      }
    }.toOption.flatten

    def insertSafe(obj: HM): Unit = {
      for {
        o <- Option(obj)
      } table.insert(o).perform[Unit]()
    }

    def countSafe(): Option[Long] = Typeable[Long].cast(table.count().perform())
  }

  implicit val typeableCursor: Typeable[Cursor[HM]] =
    new Typeable[Cursor[HM]] {
      override def cast(t: Any): Option[Cursor[HM]] = {
        if (t == null) None
        else t match {
          case c: Cursor[_] => Try(c.asInstanceOf[Cursor[HM]]).toOption
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
