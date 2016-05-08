package com.github.scalalab3.logs

import java.util

import com.rethinkdb.ast.ReqlAst
import com.rethinkdb.net.Connection

package object storage {

  type HM = util.HashMap[String, Any]

  implicit class ReqlAstExt(ast: ReqlAst)(implicit c: Connection) {
    def run[A](): A = ast.run[A](c)
  }

}
