package com.github.scalalab3.logs

import play.api.libs.json.{Json, JsValue}

import com.github.scalalab3.logs.tests.GenLog.randomLog
import com.github.scalalab3.logs.tests.DefaultSpec
import common.{Log, Level, Debug, Info}
import com.github.scalalab3.logs.common.json.LogJsonImplicits._
import com.github.scalalab3.logs.common_macro._


class JsonTest extends DefaultSpec {
  val log = randomLog()

  "test level implicits from string" >> {
    val l:Level = "asdf"
    l must_== Debug
    val now = java.time.OffsetDateTime.now
    val log2 = new Log(None, "INFO", "", "", now, "", "", "")
    val log3 = new Log(None, Info, "", "", now, "", "", "")
    log2 must_== log3
  }

  "test serializer" >> {
    val j = Json.stringify(Json.toJson(log))
    val p:JsValue = Json.parse(j)
    val lm:Option[Log] = materialize[Log](p)
    Some(log) must_== lm
  }
}
