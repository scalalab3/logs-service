package com.github.scalalab3.logs.storage

import com.github.scalalab3.logs.common.domain.{Query, Log}

trait LogStorageComponent {

  val logStorage: LogStorage

  trait LogStorage {
    def insert(log: Log): Boolean

    def count(): Long

    def filter(query: Query): List[Log]

    def lastLogs(n: Int): List[Log]
  }

}
