name := "indyref"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  ws,
  "org.twitter4j" % "twitter4j-stream" % "4.0.2"
)
