import play.sbt.PlayLayoutPlugin
import play.twirl.sbt.SbtTwirl

lazy val commonSettings = Seq(
  version := "1.0.0-SNAPSHOT",
  organization := "org.combinators",
  
  scalaVersion := "2.12.9",

  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.typesafeRepo("releases")
  ),

  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-language:implicitConversions"
  ),

  libraryDependencies ++= Seq(
    "org.combinators" %% "templating" % "1.1.0",
    "org.scalactic" %% "scalactic" % "3.0.1" % "test",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )

)

lazy val root = (Project(id = "websecbench", base = file(".")))
  .settings(commonSettings: _*)
  .dependsOn(jgitserv)
  .settings(
    moduleName := "websecbench",
    libraryDependencies ++= Seq(
      "org.combinators" %% "cls-scala" % "2.0.0+12-8d994c6b",
      "org.scalameta" %% "scalameta" % "3.4.0",
      "org.scalameta" %% "contrib" % "3.4.0"
    )

  )

lazy val jgitserv =
  Project(id = "jgitserv", base = file("jgitserv"))
    .settings(commonSettings: _*)
    .settings(
      moduleName := "jgitserv",
      libraryDependencies ++= Seq(
        "com.github.finagle" %% "finchx-core" % "0.31.0",
        "org.eclipse.jgit" % "org.eclipse.jgit" % "5.4.0.201906121030-r",
        "commons-io" % "commons-io" % "2.6",
        "ch.qos.logback" % "logback-classic" % "1.2.3"
      ),
      addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")
    )
