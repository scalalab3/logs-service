package com.github.scalalab3.logs.core

import akka.actor.{Props}
import com.github.scalalab3.logs.tests.AkkaSpec

class SystemActorTest extends AkkaSpec {
  val actorRef = system.actorOf(Props(classOf[SystemActor]))
}
