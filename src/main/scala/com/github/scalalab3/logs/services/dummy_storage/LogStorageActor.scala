package com.github.scalalab3.logs.services.dummy_storage

import akka.actor.Actor
import com.github.scalalab3.logs.common.Log
import com.github.scalalab3.logs.common.query.Query
import com.github.scalalab3.logs.storage.LogStorageComponent
import com.github.scalalab3.logs.tests.GenLog

class LogStorageActor extends Actor with LogStorageService with LogStorageComponent {

  override val logStorage: LogStorage = new LogStorage {
    val storage = (1 to 10).map(i => GenLog.randomLog()).toList

    override def count(): Long = storage.size.toLong
    override def filter(query: Query): List[Log] = storage
    override def insert(log: Log): Boolean = false
    override def lastLogs(n: Int): List[Log] = storage
  }

  override def receive: Receive = {
    case q: Query => sender() ! logStorage.filter(q)
    // case Count => sender() ! logStorage.count()
    // case Insert(log) => logStorage.insert(log)
    // ...
  }
}

// case object Count
// case class Insert(log: Log)
