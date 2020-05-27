package test

import akka.actor.ActorSystem
import com.typesafe.scalalogging.StrictLogging
import pureconfig.ConfigSource
import test.actors.{FileReader, WritingActor}
import pureconfig._
import pureconfig.generic.auto._
import scala.concurrent.ExecutionContext

object Main extends App with StrictLogging{

  val settings: FileSettings = ConfigSource.default.load[FileSettings] match {
    case Left(err) =>
      logger.info(s"Inconsistent settings cause: $err")
      sys.exit()
    case Right(value) => value
  }

  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = system.dispatcher

  val writingActor = system.actorOf(WritingActor.props, "writingActor")
  val fileListener = system.actorOf(FileReader.props(writingActor, settings), "fileListener")

}
