ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.3"

lazy val root = (project in file("."))
  .settings(
    name := "CloudSimulator"
  )

val logbackVersion = "1.4.4"

libraryDependencies ++= Seq(
  "org.cloudsimplus" % "cloudsimplus" % "7.3.0",
  "ch.qos.logback" % "logback-core" % logbackVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "com.typesafe" % "config" % "1.4.2",
)
