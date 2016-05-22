package com.github.scalalab3.logs.services

import akka.actor.{ActorRef, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.github.scalalab3.logs.common.Log
import com.github.scalalab3.logs.storage.LogStorageComponent

class ChangesActor(storage: LogStorageComponent, wsActor: ActorRef)(implicit mat: ActorMaterializer) extends AbstractService {

  val NUMBER_OF_LAST_LOGS = 10
  implicit val system = context.system

  private val source: Source[Log, Unit] = Source.fromIterator(() => storage.logStorage.changesCursor())

  val sink = Sink.actorRef(wsActor, "sent")

  (source to sink) run()

  override def receive: Receive = PartialFunction.empty
}

object ChangesActor {
  def props(storage: LogStorageComponent, wsActor: ActorRef)(implicit mat: ActorMaterializer)
    = Props(new ChangesActor(storage, wsActor))
}