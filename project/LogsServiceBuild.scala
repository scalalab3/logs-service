import sbt._
import sbt.Keys._
import sbtassembly.AssemblyPlugin.autoImport._

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

  val akkaV = "2.4.4"
  val specsV = "3.7.2"
  val baseDeps = Seq(
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

  val testDeps = Seq(
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "org.specs2" %% "specs2-core" % specsV,
    "org.specs2" %% "specs2-matcher-extra" % specsV
  )

  val commonSettings = baseSettings ++ Seq(
    organization := "com.github.scalalab3",
    version := "0.0.1",
    licenses +=("MIT", url("http://opensource.org/licenses/MIT")),
    libraryDependencies ++= baseDeps
  )

  val mainSettings = commonSettings ++ Seq(
    mainClass in assembly := Some("com.github.scalalab3.logs.services.Boot"),
    test in assembly := {}
  )

  def makeProject(name: String, path: Option[String] = None,
    settings:Seq[sbt.Def.Setting[_]] = commonSettings) = {
    Project(
      id = name,
      base = file(path getOrElse name),
      settings = settings
    )
  }

  lazy val common_macro = makeProject("common_macro")

  lazy val common = makeProject("common")
    .dependsOn(common_macro)

  lazy val tests = makeProject("tests")
    .settings( libraryDependencies ++= testDeps )
    .dependsOn(common_macro, common)

  lazy val parser = makeProject("parser")
    .dependsOn(common, tests % "test")
    .settings( libraryDependencies += "com.codecommit" %% "gll-combinators" % "2.2" )

  lazy val storage = makeProject("storage")
    .dependsOn(common, common_macro, tests % "test")
    .settings( libraryDependencies += "com.rethinkdb" % "rethinkdb-driver" % "2.3.0" )

  lazy val main = makeProject("main", Some("."), mainSettings)
    .dependsOn(common, common_macro, parser, storage, tests)
    .aggregate(common, common_macro, parser, storage, tests)
}
