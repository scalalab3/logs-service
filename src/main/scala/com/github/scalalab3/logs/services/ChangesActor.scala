package com.github.scalalab3.logs.services

import akka.actor.{Actor, ActorRef, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.github.scalalab3.logs.common.LogChanges
import com.github.scalalab3.logs.http.WsApi
import com.github.scalalab3.logs.storage.LogStorageComponent
import com.rethinkdb.net.Cursor

class ChangesActor(storage: LogStorageComponent, wsPort: Int) extends Actor {

  import scala.collection.JavaConverters._

  val NUMBER_OF_LAST_LOGS = 10
  implicit val system = context.system

  private val source: Source[LogChanges, Unit] = Source.fromIterator(() => getChangesIterator)

  private val wsActor: ActorRef = system.actorOf(Props(classOf[WsApi], wsPort), "ws-actor")
  val sink = Sink.actorRef(wsActor, "sent")

  implicit val mat = ActorMaterializer()
  (source to sink) run()


  def getChangesIterator = {
    val cursor: Cursor[Any] = storage.logStorage.changesCursor()
    val iterator: Iterator[Any] = cursor.iterator().asScala
    iterator.map(_ => getLogChanges)
  }

  def getLogChanges = LogChanges(storage.logStorage.lastLogs(NUMBER_OF_LAST_LOGS))

  override def receive: Receive = {
    case x: Any => println(x)
  }
}