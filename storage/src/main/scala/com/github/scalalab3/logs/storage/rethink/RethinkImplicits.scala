package com.github.scalalab3.logs.storage.rethink

import java.util

import com.github.scalalab3.logs.common.Slice
import com.github.scalalab3.logs.common_macro._
import com.github.scalalab3.logs.storage.rethink.TypeableImplicits._
import com.rethinkdb.RethinkDB
import com.rethinkdb.ast.ReqlAst
import com.rethinkdb.gen.ast.{Db, ReqlFunction1, Table}
import com.rethinkdb.net.{Connection, Cursor}
import shapeless.Typeable

import scala.collection.JavaConverters._
import scala.util.Try
import scalaz.Scalaz._
import scalaz._

object RethinkImplicits {

  implicit class ReqlAstExt(ast: ReqlAst)(implicit c: Connection) {
    @inline def perform[A](): A = ast.run[A](c)
  }

  private def isExists(name: String, optList: Option[util.List[_]]): Boolean = {
    optList.exists(_.contains(name))
  }

  /** r */
  implicit class RethinkDBExt(r: RethinkDB)(implicit c: Connection) {
    def dbList = cast[util.List[_]].apply(r.dbList())

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
    def tableList = cast[util.List[_]].apply(db.tableList())

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
    val filter = (func1: ReqlFunction1) => table.filter(func1).some
    val slice = (slice: Slice) => TableSliceToReqlExpr(table, slice).some

    def kleisli[T](implicit fromMap: HM => Option[T]) = cast[Cursor[HM]] >=> toList >=> listModify[T]

    def filterSafe[T](implicit fromMap: HM => Option[T]): ReaderT[Option, ReqlFunction1, List[T]] =
      kleisli[T].composeK(filter)

    def sliceSafe[T](implicit fromMap: HM => Option[T]): ReaderT[Option, Slice, List[T]] =
      kleisli[T].composeK(slice)

    def changesSafe[T](implicit fromMap: HM => Option[T]): Option[Iterator[T]] = {
      cast[Cursor[HM]] >=> toIterator >=> iteratorModify[T]
    }.run(table.changes())

    // `true` if successful insert
    def insertSafe(map: HM): Boolean = cast[HM]
      .andThenK(_.get(ReqlConstants.inserted).some)
      .run(table.insert(map))
      .contains(1L)

    def insertSafe[T](obj: T)(implicit toMap: T => HM): Boolean = insertSafe(toMap(obj))

    def countSafe(): Option[Long] = cast[Long].run(table.count())

    def indexList = cast[util.List[_]].run(table.indexList())

    def indexCreateSafe(name: String): Unit = {
      if (!isExists(name, indexList)) {
        table.indexCreate(name).perform()
        table.indexWait(name).perform()
      }
    }
  }

  /** Utils */
  def cast[T: Typeable](implicit c: Connection): Kleisli[Option, ReqlAst, T] = Kleisli {
    ast => Try(Typeable[T].cast(ast.perform())).toOption.flatten
  }

  val toList: Kleisli[Option, Cursor[HM], List[HM]] = Kleisli {
    _.toList.asScala.toList.some
  }

  val toIterator: Kleisli[Option, Cursor[HM], Iterator[HM]] = Kleisli {
    _.iterator.asScala.flatMap { next =>
      val map = next.get(ReqlConstants.newVal)
      Typeable[HM].cast(map)
    }.some
  }

  def listModify[T](implicit fromMap: HM => Option[T]): Kleisli[Option, List[HM], List[T]] = Kleisli {
    _.flatMap(fromMap(_)).some
  }

  def iteratorModify[T](implicit fromMap: HM => Option[T]): Kleisli[Option, Iterator[HM], Iterator[T]] = Kleisli {
    _.flatMap(fromMap(_)).some
  }
}