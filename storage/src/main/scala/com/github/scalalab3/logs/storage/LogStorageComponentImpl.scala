package com.github.scalalab3.logs.storage

import com.github.scalalab3.logs.common.{Log, Query}
import com.github.scalalab3.logs.common_macro.ToMap._
import com.github.scalalab3.logs.common_macro._
import com.github.scalalab3.logs.storage.rethink.RethinkImplicits._
import com.github.scalalab3.logs.storage.rethink.{LogToRethinkConverter, QueryToReqlFunction1, RethinkContext}

import scala.collection.JavaConverters._
import scala.util.Success

trait LogStorageComponentImpl extends LogStorageComponent {

  override val logStorage: LogStorage

  class LogStorageImpl(implicit r: RethinkContext) extends LogStorage {
    private val toRF1 = QueryToReqlFunction1

    private implicit val converter = new LogToRethinkConverter
    private implicit val c = r.connect

    private def toMap(log: Log): HM = toHashMap(log)

    private def fromMap(map: HM): Option[Log] = materialize[Log](map)

    // impl
    override def insert(log: Log): Unit = for {
      l <- Option(log)
    } r.table.foreach(_.insertSafe(toMap(log)))

    override def count(): Long = r.table.flatMap(_.countSafe()).getOrElse(0L)

    override def filter(query: Query): List[Log] = {
      val predicate = toRF1(query)
      r.table
        .flatMap(_.filterSafe(predicate))
        .map(_.toList.asScala)
        .map(_.flatMap(fromMap).toList)
        .getOrElse(Nil)
    }

    override def lastLogs(n: Int): List[Log] = n match {
      case v if v > 0 =>
        r.table
          .flatMap(_.cursorSafe())
          .map(_.toList.asScala)
          .map(_.flatMap(fromMap))
          .map(_.sortBy(_.timestamp).reverse)
          .map(_.take(n).toList)
          .getOrElse(Nil)
      case _ => Nil
    }

  }

}