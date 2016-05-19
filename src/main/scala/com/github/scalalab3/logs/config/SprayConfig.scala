package com.github.scalalab3.logs.config

import com.github.scalalab3.logs.common.config.ConfigLoad
import com.github.scalalab3.logs.config.Keys._
import com.github.scalalab3.logs.config.Values._

case class SprayConfig(host: String, port: Int)

object SprayConfig {
  private val config = new ConfigLoad()

  def load(): SprayConfig = SprayConfig(config.getString(host, defaultHost), config.getInt(port, defaultPort))

  val default = SprayConfig(host = defaultHost, port = defaultPort)
}