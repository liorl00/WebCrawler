package test.actors

import test.actors.CrawlerActor.Analyze
import test.actors.FileReader.AwaitingCompleted
import test.actors.WritingActor.{SuccessfulWriting, WritingError}
import akka.actor.{Actor, ActorRef, Props}
import com.typesafe.scalalogging.StrictLogging
import org.jsoup.Jsoup

import scala.collection.JavaConverters._

class CrawlerActor(writingActor: ActorRef) extends Actor with StrictLogging {

  override def preStart(): Unit = {
    logger.info(s"${self.path.name} starts working")
    context.parent ! AwaitingCompleted
  }

  override def postStop(): Unit = logger.info(s"Actor - ${self.path.name} stopped analyzing")

  override def receive: Receive = {

    case Analyze(str) =>
      try {
        val info = Jsoup.connect(s"https://$str").maxBodySize(5000).timeout(10000).get()
        val title = info.title()
        val content = info.getElementsByTag("meta").iterator().asScala.map(
          e => (e.attr("name"), e.attr("content"))
        ).toMap
        val key = content.keys.toList
        val description = content.values.toList
        writingActor ! SuccessfulWriting(str, title, key, description)
        context.parent ! AwaitingCompleted
      } catch {
        case e: Exception =>
          writingActor ! WritingError(str, e.toString)
          context.parent ! AwaitingCompleted
      }

  }

}

object CrawlerActor {

  final case class Analyze(str: String)

  def props(writingActor: ActorRef) = Props(new CrawlerActor(writingActor))

}
