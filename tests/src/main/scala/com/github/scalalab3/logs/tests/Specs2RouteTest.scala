package com.github.scalalab3.logs.tests

import org.specs2.specification.AfterAll
import spray.testkit.RouteTest

trait Specs2RouteTest extends RouteTest with Specs2Interface  with AfterAll {
  def afterAll() = system.terminate
}
