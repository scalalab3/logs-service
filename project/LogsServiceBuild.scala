import sbt.Keys._
import sbt._

object LogsServiceBuild extends Build {

  val baseSettings = Seq(
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-language:existentials",
      "-language:experimental.macros",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-unchecked",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Xfuture",
      "-Xlint"
      // ,"-Ymacro-debug-lite"
    ),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += Resolver.sonatypeRepo("releases"),
    scalaVersion := "2.11.8"
  )

  val baseDeps = {
    val akkaV = "2.4.4"
    val specsV = "3.7.2"
    Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaV,
      "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
      "org.specs2" %% "specs2-core" % specsV % "test",
      "org.specs2" %% "specs2-matcher-extra" % specsV % "test",
      "com.chuusai" %% "shapeless" % "2.3.0",
      "org.scala-lang" % "scala-reflect" % "2.11.8",
      "org.scalaz" %% "scalaz-core" % "7.2.2",
      "com.typesafe" % "config" % "1.3.0",
      "org.slf4j" % "slf4j-log4j12" % "1.7.21"
    )
  }

  val commonSettings = baseSettings ++ Seq(
    organization := "com.github.scalalab3",
    version := "0.0.1",
    licenses +=("MIT", url("http://opensource.org/licenses/MIT")),
    libraryDependencies ++= baseDeps
  )

  def makeProject(name: String, path: Option[String] = None) = {
    Project(
      id = name,
      base = file(path getOrElse name),
      settings = commonSettings
    )
  }

  lazy val common_macro = makeProject("common_macro")

  lazy val common = makeProject("common")
    .dependsOn(common_macro)

  lazy val tests = makeProject("tests")
    .dependsOn(common_macro, common)

  lazy val core = makeProject("core")
    .dependsOn(common, tests % "test")

  lazy val parser = makeProject("parser")
    .dependsOn(common, tests % "test")
    .settings( libraryDependencies += "com.codecommit" %% "gll-combinators" % "2.2" )

  lazy val storage = makeProject("storage")
    .dependsOn(common, common_macro, tests % "test")
    .settings( libraryDependencies += "com.rethinkdb" % "rethinkdb-driver" % "2.3.0" )

  lazy val ui = makeProject("ui")
    .dependsOn(common, tests % "test")

  lazy val analytics = makeProject("analytics")
    .dependsOn(common, tests % "test")

  lazy val main = makeProject("main", Some("."))
    .aggregate(common, common_macro, core, parser, storage, ui, analytics, tests)
}
