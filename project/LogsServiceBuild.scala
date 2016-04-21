import sbt._
import sbt.Keys._

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
      "com.chuusai" %% "shapeless" % "2.2.4",
      "org.scala-lang" % "scala-compiler" % "2.11.8"
    )
  }

  val commonSettings = baseSettings ++ Seq(
    organization := "com.github.scalalab3",
    version := "0.0.1",
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    libraryDependencies ++= baseDeps
  )

  def makeProject(name:String, path:Option[String] = None) = {
    Project(
      id=name,
      base=file(path getOrElse name),
      settings = commonSettings
    )
  }

  lazy val common_macro = makeProject("common_macro")

  lazy val common = makeProject("common")
    .dependsOn(common_macro)

  lazy val core = makeProject("core")
    .dependsOn(common)


  lazy val parser = makeProject("parser")
    .dependsOn(common)

  lazy val storage = makeProject("storage")
    .dependsOn(common)

  lazy val ui = makeProject("ui")
    .dependsOn(common)

  lazy val analytics = makeProject("analytics")
    .dependsOn(common)

  lazy val main = makeProject("main", Some("."))
    .aggregate(common, common_macro, core, parser, storage, ui, analytics)
}
