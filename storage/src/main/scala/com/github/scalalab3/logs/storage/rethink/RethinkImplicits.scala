package com.github.scalalab3.logs.storage.rethink

import java.util

import com.github.scalalab3.logs.common.Slice
import com.github.scalalab3.logs.common_macro._
import com.rethinkdb.RethinkDB
import com.rethinkdb.ast.ReqlAst
import com.rethinkdb.gen.ast.{Db, ReqlFunction1, Table}
import com.rethinkdb.net.{Connection, Cursor}
import shapeless.Typeable

import scala.collection.JavaConverters._
import scala.util.Try

object RethinkImplicits {

  implicit class ReqlAstExt(ast: ReqlAst)(implicit c: Connection) {
    @inline def perform[A](): A = ast.run[A](c)
  }

  private def isExists(name: String, optList: Option[util.List[_]]): Boolean = {
    val res = for {
      list <- optList
    } yield list.contains(name)

    res.getOrElse(false)
  }

  /** r */
  implicit class RethinkDBExt(r: RethinkDB)(implicit c: Connection) {
    def dbList = Typeable[util.List[_]].cast(r.dbList().perform())

    def dbSafe(name: String): Db = {
      if (!isExists(name, dbList)) r.dbCreate(name).perform()
      r.db(name)
    }

    def dbDropSafe(name: String): Unit = {
      if (isExists(name, dbList)) r.dbDrop(name).perform()
    }
  }

  /** Db */
  implicit class DbExt(db: Db)(implicit c: Connection) {
    def tableList = Typeable[util.List[_]].cast(db.tableList().perform())

    def tableSafe(name: String): Table = {
      if (!isExists(name, tableList)) db.tableCreate(name).perform()
      db.table(name)
    }

    def tableDropSafe(name: String): Unit = {
      if (isExists(name, tableList)) db.tableDrop(name).perform()
    }
  }

  /** Table */
  implicit class TableExt(table: Table)(implicit c: Connection) {
    def cursorSafe(): Option[Cursor[HM]] = Typeable[Cursor[HM]].cast(table.perform())

    def changesSafe(): Option[Cursor[HM]] = Typeable[Cursor[HM]].cast(table.changes().perform())

    def filterSafe(func1: ReqlFunction1): Option[Cursor[HM]] = Try {
      Typeable[Cursor[HM]].cast {
        table.filter(func1).perform()
      }
    }.toOption.flatten

    // `true` if successful insert
    def insertSafe(map: HM): Boolean = {
      val res = for {
        m <- Typeable[HM].cast(table.insert(map).perform())
        i <- Option(m.get(ReqlConstants.inserted))
      } yield i

      res.contains(1L)
    }

    def insertSafe[T](obj: T)(implicit toMap: T => HM): Boolean = insertSafe(toMap(obj))

    def countSafe(): Option[Long] = Typeable[Long].cast(table.count().perform())

    def indexList = Typeable[util.List[_]].cast(table.indexList().perform())

    def indexCreateSafe(name: String): Unit = {
      if (!isExists(name, indexList)) {
        table.indexCreate(name).perform()
        table.indexWait(name).perform()
      }
    }

    def sliceSafe(slice: Slice) = Try {
      Typeable[Cursor[HM]].cast {
        TableSliceToReqlExpr(table, slice).perform()
      }
    }.toOption.flatten
  }

  /** Cursor */
  implicit class CursorExt(cursor: Cursor[HM]) {
    def toScalaList[T](implicit fromMap: HM => Option[T]): List[T] =
      cursor.toList.asScala.flatMap(fromMap(_)).toList

    def toScalaIterator[T](implicit fromMap: HM => Option[T]): Iterator[T] = {
      cursor.iterator.asScala.flatMap { next =>
        val map = next.get(ReqlConstants.newVal)
        Typeable[HM].cast(map)
      }.flatMap(fromMap(_))
    }
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

      override def describe: String = "Cursor[util.HashMap[String, Any]]"
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

      override def describe: String = "util.List[_]"
    }

  implicit val typeableMap: Typeable[HM] =
    new Typeable[HM] {
      override def cast(t: Any): Option[HM] = {
        if (t == null) None
        else t match {
          case c: util.Map[_, _] => Try(c.asInstanceOf[HM]).toOption
          case _ => None
        }
      }

      override def describe: String = "util.HashMap[String, Any]"
    }
}
