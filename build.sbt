import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

name := "indyref"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  ws,
  "org.twitter4j" % "twitter4j-stream" % "4.0.2"
)

// Don't include documentation in artifact
doc in Compile <<= target.map(_ / "none")

maintainer in Docker := "Graham Tackley <graham.tackley@theguardian.com>"

dockerExposedPorts in Docker := List(9000)
