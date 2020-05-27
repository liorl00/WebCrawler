package test.actors

import test.actors.CrawlerActor.Analyze
import test.actors.FileReader.{AwaitingCompleted, ReadFile}
import akka.actor.{Actor, ActorRef, Props}
import com.typesafe.scalalogging.StrictLogging
import test.FileSettings
import scala.io.Source

class FileReader(settings: FileSettings, writingActor: ActorRef) extends Actor with StrictLogging{

  override def preStart(): Unit =
    logger.info("File reader started")
    self ! ReadFile(settings.fileSettings.fileName)

  override def receive: Receive = {

    case ReadFile(fileName) =>
      val resource = Source.fromResource(fileName)
      (0 until settings.fileSettings.batchSize).foreach(i =>
        context.actorOf(CrawlerActor.props(writingActor), s"crawlerActor$i"))
      context.become(workingCycle(resource.getLines().toList))
      resource.close()

  }

  def workingCycle(list: List[String]): Receive = {
    case AwaitingCompleted  =>
      logger.info(s"Urls remaining - ${list.length}")
      list.headOption match {
        case Some(value) =>
          logger.info(s"New url requested from ${sender().path.name}")
          sender() ! Analyze(value)
          context.become(workingCycle(list.drop(1)))
        case None =>
          logger.info("No more element to write. Writing completed")
          context.stop(sender())
      }
    case _ =>
  }
}

object FileReader {

  def props(writingActor: ActorRef,
            settings: FileSettings) = Props(new FileReader(settings, writingActor))

  final case class ReadFile(str: String)

  final case class SendBatch(list: List[String])

  final case object AwaitingCompleted

  final case object SendAnotherOne

}

