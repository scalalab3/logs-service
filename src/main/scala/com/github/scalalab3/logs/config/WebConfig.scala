package com.github.scalalab3.logs.config

import com.github.scalalab3.logs.config.WebKeys._
import com.typesafe.config.ConfigFactory

case class WebConfig(host: String, port: Int, wsPort: Int)

object WebConfig {
  private val config = ConfigFactory.load()

  def load(): WebConfig = WebConfig(config.getString(host), config.getInt(port), config.getInt(wsPort))
}