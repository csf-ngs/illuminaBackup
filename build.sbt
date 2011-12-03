import AssemblyKeys._ 

organization := "at.ac.csfg"

name := "illuminaBackup"

version := "0.5"

scalaVersion := "2.9.1"

seq(assemblySettings: _*)

jarName in assembly := "illuminaBackup.jar"

libraryDependencies += "com.beust" % "jcommander" % "1.19"

testOptions in Test += Tests.Argument("html", "console")

git.remoteRepo := "git@github.com:csf-ngs/illuminaBackup.git"

libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.6.2",   
    "ch.qos.logback" % "logback-core" % "1.0.0",
    "ch.qos.logback" % "logback-classic" % "1.0.0"
)

libraryDependencies ++= Seq(
    "org.specs2" %% "specs2" % "1.6.1" % "test",
    "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test",
    "org.mockito" % "mockito-all" % "1.8.5" % "test",
    "junit" % "junit" % "4.8" % "test",
    "org.pegdown" % "pegdown" % "1.0.2" % "test"
)
