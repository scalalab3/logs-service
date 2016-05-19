package com.github.scalalab3.logs.storage

import com.github.scalalab3.logs.common.Log
import com.github.scalalab3.logs.common.query.Query
import com.github.scalalab3.logs.common_macro.ToMap._
import com.github.scalalab3.logs.common_macro._
import com.github.scalalab3.logs.storage.rethink.RethinkImplicits._
import com.github.scalalab3.logs.storage.rethink.{LogToRethink, QueryToReqlFunction1, RethinkContext}
import com.rethinkdb.net.Cursor

trait LogStorageComponentImpl extends LogStorageComponent {

  override val logStorage: LogStorage

  class LogStorageImpl(implicit r: RethinkContext) extends LogStorage {

    private implicit val converter = LogToRethink
    private implicit val connection = r.connect

    private implicit val toMap: Log => HM = toHashMap(_)
    private implicit val fromMap: HM => Option[Log] = materialize[Log]

    override def insert(log: Log): Boolean = r.table().insertSafe[Log](log)

    override def count(): Long = r.table().countSafe().getOrElse(0L)

    override def filter(query: Query): List[Log] = {
      val predicate = QueryToReqlFunction1(query)
      r.table()
        .filterSafe(predicate)
        .map(_.toScalaList[Log])
        .getOrElse(Nil)
    }

    override def lastLogs(n: Int): List[Log] = n match {
      case v if v > 0 =>
        r.table()
          .cursorSafe()
          .map(_.toScalaList[Log])
          .map(_.sortBy(_.dateTime).reverse)
          .map(_.take(n))
          .getOrElse(Nil)
      case _ => Nil
    }

    override def changesCursor(): Cursor[Any] = r.table().changes.run(connection)
  }
}