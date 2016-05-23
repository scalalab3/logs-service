package com.github.scalalab3.logs.storage

import com.github.scalalab3.logs.common.{Index, Slice, Log}
import com.github.scalalab3.logs.common.query.Query

trait LogStorageComponent {

  val logStorage: LogStorage

  trait LogStorage {
    def insert(log: Log): Boolean

    def count(): Long

    def filter(query: Query): List[Log]

    def slice(slice: Slice): List[Log]

    def indexCreate(index: Index): Unit

    def changesCursor(): Iterator[Log]
  }

}
