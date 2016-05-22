package com.github.scalalab3.logs.common.offset

case class OrderBy(index: Index = Index(), ordering: Ordering = Desc)

sealed trait Ordering

case object Desc extends Ordering
case object Asc extends Ordering
