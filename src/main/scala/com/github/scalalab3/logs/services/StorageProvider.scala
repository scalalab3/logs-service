package com.github.scalalab3.logs.services

import akka.actor.ActorRef

trait StorageProvider {
  def dbService: ActorRef
}