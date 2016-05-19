package com.github.scalalab3.logs.components

import com.github.scalalab3.logs.common.query.Query
import com.github.scalalab3.logs.parser.QueryParserImpl

trait QueryServiceComponentImpl extends QueryServiceComponent {

  override val queryService: QueryService = new QueryServiceImpl

  class QueryServiceImpl extends QueryService {
    override def queryOf(query: String): Option[Query] = QueryParserImpl.parse(query).toOption
  }

}
