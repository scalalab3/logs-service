package com.github.scalalab3.logs.storage.rethink

case class RethinkConfig(host: String = "localhost",
                         port: Int = 28015,
                         user: String = "admin",
                         password: String = "",
                         dbName: String = "test",
                         tableName: String = "test")
