package com.github.scalalab3.logs.common.config

import com.typesafe.config.{ConfigFactory, Config}

import scala.util.Try

class ConfigLoad(config: Config = ConfigFactory.load()) {
  def getString(key: String, defaultValue: String) = Try(config.getString(key)).getOrElse(defaultValue)
  def getInt(key: String, defaultValue: Int) = Try(config.getInt(key)).getOrElse(defaultValue)
}
