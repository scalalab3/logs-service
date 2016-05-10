package com.github.scalalab3.logs.storage

import com.github.scalalab3.logs.common.{Query, Log}
import com.github.scalalab3.logs.common_macro.ToMap._
import com.github.scalalab3.logs.common_macro._
import com.github.scalalab3.logs.storage.rethink.RethinkImplicits._
import com.github.scalalab3.logs.storage.rethink.{RethinkContext, LogToRethinkConverter, QueryToReqlFunction1}
import com.rethinkdb.net.Cursor
import shapeless.Typeable

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
    override def insert(log: Log): Unit = r.table.foreach(_.insert(toMap(log)).perform[Unit]())

    override def count(): Long = r.table.map(_.count().perform[Long]()).getOrElse(0L)

    override def filter(query: Query): List[Log] = {

      toRF1(query) match {
        case Success(p) =>
          r.table.flatMap { t =>
            Typeable[Cursor[HM]].cast(t.filter(p).perform())
              .map(_.toList.asScala)
              .map(_.flatMap(fromMap).toList)
          }.getOrElse(Nil)
        case _ => Nil
      }
    }

    override def lastLogs(n: Int): List[Log] = n match {
      case v if v > 0 =>
        r.table.flatMap { t =>
          Typeable[Cursor[HM]].cast(t.perform())
            .map(_.toList.asScala)
            .map(_.flatMap(fromMap))
            .map(_.sortBy(_.timestamp).reverse)
            .map(_.take(n).toList)
        }
          .getOrElse(Nil)
      case _ => Nil
    }

  }

}