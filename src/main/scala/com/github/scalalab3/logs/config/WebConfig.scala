package com.github.scalalab3.logs.config

import com.github.scalalab3.logs.common.Environment
import com.github.scalalab3.logs.config.WebKeys._
import com.typesafe.config.{Config, ConfigFactory}

case class WebConfig(host: String, port: Int, wsPort: Int)

object WebConfig {
  private val config = ConfigFactory.load()

  def apply(): WebConfig = load(config)

  def apply(env: Environment): WebConfig = load(config.getConfig(env.toKey).withFallback(config))

  def load(config: Config): WebConfig = WebConfig(
    config.getString(sprayHost),
    config.getInt(sprayPort),
    config.getInt(wsPort)
  )
}