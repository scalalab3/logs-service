package com.github.scalalab3.logs.storage

import com.github.scalalab3.logs.common.Log

trait LogStorageComponent {

  val logStorage: LogStorage

  trait LogStorage {
    def insert(log: Log): Unit

    def count(): Long

    def filter(query: String): List[Log]

    def lastLogs(n: Int): List[Log]
  }

}
