package com.github.scalalab3.logs.storage

import com.github.scalalab3.logs.common.Log
import com.github.scalalab3.logs.common_macro.ToMap._
import com.github.scalalab3.logs.common_macro._
import com.github.scalalab3.logs.storage.rethink.RethinkContext._
import com.github.scalalab3.logs.storage.rethink.RethinkImplicits._
import com.github.scalalab3.logs.storage.rethink.{LogToRethinkConverter, StringQueryToReqlFunction1}
import com.rethinkdb.net.Cursor
import shapeless.Typeable

import scala.collection.JavaConverters._
import scala.util.Success

trait LogStorageComponentImpl extends LogStorageComponent {

  override val logStorage: LogStorage = new LogStorageImpl

  class LogStorageImpl extends LogStorage {
    private val toRF1 = StringQueryToReqlFunction1

    private implicit val converter = new LogToRethinkConverter

    private def toMap(log: Log): HM = toHashMap(log)
    private def fromMap(map: HM): Option[Log] = materialize[Log](map)

    // impl
    override def insert(log: Log): Unit = table.foreach(_.insert(toMap(log)).perform[Unit]())

    override def count(): Long = table.map(_.count().perform[Long]()).getOrElse(0L)

    override def filter(query: String): List[Log] = {

      toRF1(query) match {
        case Success(p) =>
          table.flatMap { t =>
            Typeable[Cursor[HM]]
              .cast(t.filter(p).perform())
              .map(_.toList.asScala)
              .map(_.flatMap(fromMap).toList)
          }.getOrElse(Nil)
        case _ => Nil
      }
    }

    override def lastLogs(n: Int): List[Log] = n match {
      case v if v > 0 =>
        table.flatMap { t =>
          Typeable[Cursor[HM]]
            .cast(t.perform())
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