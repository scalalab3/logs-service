package com.github.scalalab3.logs.json

import com.github.scalalab3.logs.tests.GenLog.randomLog
import com.github.scalalab3.logs.tests.LogJsonSpecification
import org.specs2.specification.Scope

class LogJsonProtocolTest extends LogJsonSpecification {

  trait RandomLog extends Scope {
    val toJson = LogJsonProtocol.format
    val v = randomLog()
  }

  "LogJsonFormat should write a log" in new RandomLog {
    val jsv = toJson.write(v).prettyPrint

    jsv must aLogWith(name = v.name, id = v.id.get.toString, dateTime = v.dateTime.toString)
  }
}
