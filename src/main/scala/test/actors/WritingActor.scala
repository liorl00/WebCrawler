package test.actors

import java.io.{BufferedWriter, File, FileWriter}
import test.actors.WritingActor.{SuccessfulWriting, WritingError}
import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.StrictLogging

class WritingActor extends Actor with StrictLogging{

  val successFile = new File("successResult.txt")
  val failureFile = new File("failureResult.txt")

  override def receive: Receive = {

    case SuccessfulWriting(url, title, info, description) =>
      val bw = new BufferedWriter(new FileWriter(successFile, true))
      bw.write(s"$url: title = $title, keywords = ${info.mkString}, description = ${description.mkString} \n")
      logger.info(s"$url was written by ${sender().path.name} / SUCCESS")
      bw.close()

    case WritingError(url, err) =>
      val bw = new BufferedWriter(new FileWriter(failureFile, true))
      bw.write(s"$url: error = $err \n")
      logger.info(s"$url was written by ${sender().path.name} / ERROR")
      bw.close()
  }
}

object WritingActor {

  final case class SuccessfulWriting(url: String, title: String, keyWords: List[String], description: List[String])

  final case class WritingError(url: String, err: String)

  def props = Props(new WritingActor())
}
