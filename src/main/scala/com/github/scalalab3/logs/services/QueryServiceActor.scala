package com.github.scalalab3.logs.services

import akka.actor.Actor
import com.github.scalalab3.logs.components.QueryServiceComponentImpl

class QueryServiceActor extends Actor with QueryServiceRoute with QueryServiceComponentImpl {

  override def actorRefFactory = context

  def receive = runRoute(queryRoute)

}
