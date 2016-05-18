package com.github.scalalab3.logs.services

import akka.actor.Props
import com.github.scalalab3.logs.tests.AkkaSpec

class SystemActorTest extends AkkaSpec {
  "Test SystemActor" >> {
    val actorRef = system.actorOf(Props(classOf[SystemActor]), "system-actor")

    "send Any message" in {
      actorRef ! "test message"
      ok
    }
  }
}
