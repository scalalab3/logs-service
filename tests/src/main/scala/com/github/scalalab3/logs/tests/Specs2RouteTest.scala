package com.github.scalalab3.logs.tests

import org.specs2.specification.AfterAll
import spray.testkit.RouteTest

import scala.concurrent.duration.DurationInt

trait Specs2RouteTest extends RouteTest with Specs2Interface with AfterAll {
  implicit val routeTestTimeout = RouteTestTimeout(5.second)
  def afterAll() = system.terminate
}
