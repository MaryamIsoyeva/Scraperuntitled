name := "ScraperUpdate"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.14",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.akka" %% "akka-http" % "10.1.2",
  "de.heikoseeberger" %% "akka-http-circe" % "1.21.0",
  "com.typesafe.akka" %% "akka-http-xml" % "10.1.5",
  "io.circe" %% "circe-core" % "0.9.3",
  "io.circe" %% "circe-generic" % "0.9.3",
  "io.circe" %% "circe-parser" % "0.9.3",
  "io.circe" %% "circe-java8" % "0.9.3",
  "net.ruippeixotog" %% "scala-scraper" % "2.1.0",
  "org.scala-lang.modules" %% "scala-xml" % "1.1.0",
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
  "org.postgresql" % "postgresql" % "42.2.2",
  "com.github.tminglei" %% "slick-pg" % "0.16.2",
  "com.github.tminglei" %% "slick-pg_circe-json" % "0.16.3",
  "com.github.tototoshi" %% "scala-csv" % "1.3.6"


)

addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
)

