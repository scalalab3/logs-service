package com.github.scalalab3.logs.config

import com.github.scalalab3.logs.config.SprayKeys._
import com.typesafe.config.ConfigFactory

case class SprayConfig(host: String, port: Int)

object SprayConfig {
  private val config = ConfigFactory.load()

  def load(): SprayConfig = SprayConfig(config.getString(host), config.getInt(port))
}