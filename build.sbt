name := "versatile-library"

organization := "org.versatile-flow"

version := "0.1"

scalaVersion := "2.11.8"

lazy val sparkV = "2.2.0"
lazy val kafkaV = "1.0.1"

resolvers += "confluent.io" at "http://packages.confluent.io/maven/"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkV,
  "org.apache.spark" %% "spark-sql" % sparkV,
  "org.apache.spark" %% "spark-hive" % sparkV,
  "org.apache.kafka" %% "kafka-streams-scala" % "2.0.1",
  "io.confluent" % "kafka-streams-avro-serde" % "5.0.0",
  "com.typesafe.akka" %% "akka-http" % "10.1.3",
  "com.typesafe.akka" %% "akka-stream" % "2.5.12",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "de.heikoseeberger" %% "akka-http-circe" % "1.17.0",
  "com.twitter" %% "bijection-avro" % "0.9.5",
  "com.sksamuel.avro4s" %% "avro4s-core" % "2.0.2"
)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

val circeVersion = "0.9.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
libraryDependencies += "io.spray" %% "spray-json" % "1.3.3"

// Enable SAM
scalacOptions := Seq("-Xexperimental", "-unchecked", "-deprecation")

// Executer les tests en séquentiel
parallelExecution in Test := false

resolvers += "confluent" at "https://packages.confluent.io/maven/"
resolvers += Resolver.sonatypeRepo("public")
