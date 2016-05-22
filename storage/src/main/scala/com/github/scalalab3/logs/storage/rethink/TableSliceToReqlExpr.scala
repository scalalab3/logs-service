package com.github.scalalab3.logs.storage.rethink

import com.github.scalalab3.logs.common._
import com.github.scalalab3.logs.storage.rethink.ReqlConstants._
import com.rethinkdb.RethinkDB.r
import com.rethinkdb.gen.ast.{ReqlExpr, Table}

object TableSliceToReqlExpr {

  def apply(table: Table, slice: Slice): ReqlExpr = {

    val start = slice.offset.start
    val end = slice.offset.end

    table
      .orderBy()
      .optArg(index, orderByToReqlExpr(slice.orderBy))
      .slice(start.point, end.point)
      .optArg(leftBound, bound(start))
      .optArg(rightBound, bound(end))
  }

  private def bound(offsetBound: OffsetBound): String = {
    offsetBound.isClosed match {
      case true  => closed
      case false => open
    }
  }

  private def orderByToReqlExpr(orderBy: OrderBy): ReqlExpr = {
    val name = orderBy.index.name
    orderBy.ordering match {
      case Desc => r.desc(name)
      case Asc  => r.asc(name)
    }
  }
}
