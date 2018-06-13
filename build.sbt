
organization := "at.ac.csfg"

name := "illuminaBackup"

version := "0.6"

scalaVersion := "2.11.8"


jarName in assembly := "illuminaBackup.jar"

libraryDependencies += "com.beust" % "jcommander" % "1.19"

testOptions in Test += Tests.Argument("html", "console")


libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.7.5",   
    "ch.qos.logback" % "logback-core" % "1.0.0",
    "ch.qos.logback" % "logback-classic" % "1.0.0"
)

libraryDependencies ++= Seq(
    "org.specs2" %% "specs2-core" % "3.7.2" % "test",
    "org.specs2" %% "specs2-junit" % "3.7.2" % "test",
    "org.mockito" % "mockito-all" % "1.9.0" % "test",
    "junit" % "junit" % "4.11" % "test",
    "org.pegdown" % "pegdown" % "1.0.2" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")


