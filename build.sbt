organization := "at.ac.csfg"

name := "illuminaBackup"

version := "0.5"

scalaVersion := "2.9.1"


testFrameworks += new TestFramework("org.specs2.runner.SpecsFramework")

libraryDependencies ++= Seq(
    "org.specs2" %% "specs2" % "1.6.1",
    "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test",
    "org.mockito" % "mockito-all" % "1.8.5" % "test",
    "junit" % "junit" % "4.8" % "test"
)
