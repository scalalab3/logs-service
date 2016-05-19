package com.github.scalalab3.logs

import com.github.scalalab3.logs.storage.rethink.RethinkConfig

package object config {
  case class AppConfig(wsPort: Int = 9999)

  val appConfig = AppConfig()

  val dbConfig = new RethinkConfig(host = "localhost")
}
