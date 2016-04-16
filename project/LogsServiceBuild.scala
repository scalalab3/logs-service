import sbt._
import sbt.Keys._

object LogsServiceBuild extends Build {

  val baseSettings = Seq(
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:experimental.macros",
      "-unchecked",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Xfuture",
      "-Xlint"
    ),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += Resolver.sonatypeRepo("releases"),
    scalaVersion := "2.11.8"
  )


  lazy val main = Project(
    id = "logs-service",
    base = file("."),
    settings = baseSettings ++ Seq(
      organization := "com.github.scalalab3-logs",
      name := "logs-service",
      version := "0.0.1",
      licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
      libraryDependencies ++= {
        val akkaV = "2.4.4"
        val specsV = "3.7.2"
        Seq(
          "com.typesafe.akka" %% "akka-actor" % akkaV,
          "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
          "org.specs2" %% "specs2-core" % specsV % "test",
          "org.specs2" %% "specs2-matcher-extra" % specsV % "test"
        )
      }
    )
  )




}
