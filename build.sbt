name := "smartparking-raspberry"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++=
  {
    val akkaVersion = "2.3.2"
    val sprayVersion = "1.3.1"
    Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion
        exclude("org.scala-lang", "scala-library"),
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
        exclude("org.slf4j", "slf4j-api")
        exclude("org.scala-lang", "scala-library")
    )
  }