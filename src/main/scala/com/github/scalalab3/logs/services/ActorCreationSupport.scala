package com.github.scalalab3.logs.services

import akka.actor.{ActorRef, Props, ActorContext}

trait ActorCreationSupport {
  def create(props: Props, name: String): ActorRef
}

trait ActorContextCreationSupport extends ActorCreationSupport {
  def context: ActorContext
  override def create(props: Props, name: String) = context.actorOf(props, name)
}

trait StorageProvider {
  def dbService: ActorRef
}