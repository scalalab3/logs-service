package com.github.scalalab3.logs.common_macro

trait Converter[-T] {
  def toMap[K <: Symbol, V]: Function[(K, V), (String, Any)]
  def fromMap: Function[(String, Option[Any]), Option[Any]]
}
