package com.github.scalalab3.logs.common

import com.github.scalalab3.logs.common.util.Keys

case class Slice(orderBy: OrderBy = OrderBy(), offset: Offset)

case class Index(name: String = Keys.time)

case class OrderBy(index: Index = Index(), ordering: Ordering = Desc)

sealed trait Ordering
case object Desc extends Ordering
case object Asc extends Ordering

case class OffsetBound(point: Int, isClosed: Boolean)

case class Offset(start: OffsetBound, end: OffsetBound)

object Offset {
  implicit class OffsetExt(startOffset: OffsetBound) {
    def to(endOffset: OffsetBound) = Offset(startOffset, endOffset)
  }
}
