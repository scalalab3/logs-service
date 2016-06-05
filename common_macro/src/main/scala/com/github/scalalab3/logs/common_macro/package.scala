package com.github.scalalab3.logs

import play.api.libs.json.JsValue

package object common_macro {

  type HM = java.util.HashMap[String, Any]

  implicit def scalaMapToJavaHashMap(m: Map[String, Any]): HM = {
    val out: HM = new java.util.HashMap()
    m.foreach(kv => out.put(kv._1, kv._2))
    out
  }

  implicit def materialize[T](map: HM)(implicit ev: AnyToCC[T, HM]): Option[T] = {
    ev.fromValue(map)
  }

  implicit def materialize[T](json: JsValue)(implicit ev: AnyToCC[T, JsValue]): Option[T] = {
    ev.fromValue(json)
  }
}
