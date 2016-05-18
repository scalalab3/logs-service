package com.github.scalalab3.logs.components

import com.github.scalalab3.logs.common.query.Query

trait QueryServiceComponent {

  val queryService: QueryService

  trait QueryService {
    def queryOf(query: String): Option[Query]
  }

}
