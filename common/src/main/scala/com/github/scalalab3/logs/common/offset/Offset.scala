package com.github.scalalab3.logs.common.offset

case class Offset(startOffset: OffsetBound, endOffset: OffsetBound)

case class OffsetBound(point: Int, isClosed: Boolean)

object Offset {
  implicit class OffsetExt(startOffset: OffsetBound) {
    def to(endOffset: OffsetBound) = Offset(startOffset, endOffset)
  }
}
