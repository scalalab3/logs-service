package com.github.scalalab3.logs.storage

import com.github.scalalab3.logs.common.Log
import com.github.scalalab3.logs.common_macro.ToMap._
import com.github.scalalab3.logs.common_macro.{FromMap, HM}
import com.github.scalalab3.logs.storage.rethink.Rethink._
import com.github.scalalab3.logs.storage.rethink.{LogToRethinkConverter, StringQueryToReqlFunction1}
import com.rethinkdb.net.Cursor

import scala.collection.JavaConverters._
import scala.util.Success

class LogStorageComponentImpl() extends LogStorageComponent {

  override def logStorage: LogStorage = LogStorageImpl

  object LogStorageImpl extends LogStorage {
    val toRF1 = StringQueryToReqlFunction1

    implicit val converter = new LogToRethinkConverter

    private def toMap(log: Log): HM = toHashMap(log)
    private def fromMap(map: HM): Option[Log] = implicitly[FromMap[Log]].fromMap(map)

    // impl
    override def insert(log: Log): Unit = table.insert(toMap(log)).run()

    override def count(): Long = table.count().run()

    override def filter(query: String): List[Log] = {

      toRF1 (query) match {
        case Success(p) =>
          val cursor = table.filter(p).run().asInstanceOf[Cursor[HM]]
          cursor.toList.asScala.flatMap(fromMap).toList
        case _ => Nil
      }
    }

    override def lastLogs(n: Int): List[Log] = n match {
      case v if v > 0 => table.run()
        .asInstanceOf[Cursor[HM]].toList.asScala
        .flatMap(fromMap)
        .sortBy(_.timestamp).reverse
        .take(n)
        .toList
      case _ => Nil
    }

  }

}