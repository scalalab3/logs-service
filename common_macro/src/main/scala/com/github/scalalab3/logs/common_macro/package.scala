package com.github.scalalab3.logs

import java.util
import play.api.libs.json.JsValue

package object common_macro {

  type HM = util.HashMap[String, Any]

  implicit def scalaMapToJavaHashMap(m: Map[String, Any]): HM = {
    val out: HM = new util.HashMap()
    m.foreach(kv => out.put(kv._1, kv._2))
    out
  }

  implicit def materialize[T: FromMap](map: HM): Option[T] = implicitly[FromMap[T]].fromMap(map)

  implicit def materialize[T](json: JsValue): Option[T] = {
    implicitly[FromJson].fromValue(json)
    // FromJson.materializeM[T]
  }
}
