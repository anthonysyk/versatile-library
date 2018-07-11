name := "versatile-library"

organization := "org.versatile-flow"

version := "0.1"

scalaVersion := "2.11.8"

lazy val sparkV = "2.2.0"
lazy val kafkaV = "1.0.1"
val kafka_streams_scala_version = "0.2.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkV,
  "org.apache.spark" %% "spark-sql" % sparkV,
  "org.apache.spark" %% "spark-hive" % sparkV,
  "org.apache.kafka" % "kafka_2.11" % kafkaV,
  "org.apache.kafka" % "kafka-streams" % kafkaV,
  "net.manub" %% "scalatest-embedded-kafka" % "1.0.0" % Test,
  "com.lightbend" %% "kafka-streams-scala" % kafka_streams_scala_version
)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

val circeVersion = "0.9.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.3"


// Enable SAM
scalacOptions := Seq("-Xexperimental", "-unchecked", "-deprecation")