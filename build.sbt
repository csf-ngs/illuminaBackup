import AssemblyKeys._ 



organization := "at.ac.csfg"

name := "illuminaBackup"

version := "0.5"

scalaVersion := "2.10.0"

seq(assemblySettings: _*)


jarName in assembly := "illuminaBackup.jar"

libraryDependencies += "com.beust" % "jcommander" % "1.19"

testOptions in Test += Tests.Argument("html", "console")


libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.6.2",   
    "ch.qos.logback" % "logback-core" % "1.0.0",
    "ch.qos.logback" % "logback-classic" % "1.0.0"
)

libraryDependencies ++= Seq(
    "org.specs2" %% "specs2" % "1.14" % "test",
    "org.mockito" % "mockito-all" % "1.8.5" % "test",
    "junit" % "junit" % "4.8" % "test",
    "org.pegdown" % "pegdown" % "1.0.2" % "test"
)


