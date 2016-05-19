package com.github.scalalab3.logs.json

import com.github.scalalab3.logs.tests.GenLog.randomLog
import org.specs2.matcher.{JsonMatchers, JsonType, Matcher}
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class LogJsonProtocolTest extends Specification with JsonMatchers {

  trait RandomLog extends Scope {
    val toJson = LogJsonProtocol.format
    val v = randomLog()
  }

  def aLogWith(name: Matcher[JsonType], id: Matcher[JsonType], dateTime: Matcher[JsonType]): Matcher[String] =
    /("name").andHave(name) and /("id").andHave(id) and /("dateTime").andHave(dateTime)

  def haveLogs(logs: Matcher[String]*): Matcher[String] = have(allOf(logs:_*))

  "LogJsonFormat should write a log" in new RandomLog {
    val jsv = toJson.write(v).prettyPrint

    jsv must aLogWith(name = v.name, id = v.id.get.toString, dateTime = v.dateTime.toString)
  }

}
