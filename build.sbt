name := "WebCrawler"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.13.1",
  "com.typesafe.akka" %% "akka-actor" % "2.6.5",
  "com.github.pureconfig" %% "pureconfig" % "0.12.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)