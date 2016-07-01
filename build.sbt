name := "BookStoreService"

version := "1.0"

scalaVersion := "2.11.8"

lazy val versions = new {
    val finatra = "2.1.6"
    val logback = "1.1.3"
    val guice = "4.0"
    val specs2 = "2.3.12"
    val scalatest = "2.2.6"
    val mongo_driver = "1.1.1"
}

resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    "Twitter Maven" at "https://maven.twttr.com"
)

libraryDependencies += "com.twitter.finatra" % "finatra-http_2.11" % versions.finatra

// for serialized and deserialized json
libraryDependencies += "com.twitter.finatra" % "finatra-jackson_2.11" % versions.finatra

// Finatra uses the SLF4J api for framework logging
libraryDependencies += "com.twitter.finatra" % "finatra-slf4j_2.11" % versions.finatra

// Twitter highly recommend using Logback as an SLF4J binding (logging implementation).
libraryDependencies += "ch.qos.logback" % "logback-classic" % versions.logback
libraryDependencies += "org.clapper" %% "grizzled-slf4j" % "1.0.2"

//libraryDependencies += "com.twitter.finatra" % "finatra-http_2.11" % versions.finatra % "test"
libraryDependencies += "com.twitter.inject" % "inject-server_2.11" % versions.finatra % "test"
libraryDependencies += "com.twitter.inject" % "inject-app_2.11" % versions.finatra % "test"
libraryDependencies += "com.twitter.inject" % "inject-core_2.11" % versions.finatra % "test"
libraryDependencies += "com.twitter.inject" %% "inject-modules" % versions.finatra % "test"
libraryDependencies += "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test"
libraryDependencies += "com.twitter.finatra" % "finatra-jackson_2.11" % versions.finatra % "test"

libraryDependencies += "com.twitter.finatra" % "finatra-http_2.11" % versions.finatra % "test" classifier "tests"
libraryDependencies += "com.twitter.inject" % "inject-server_2.11" % versions.finatra % "test" classifier "tests"
libraryDependencies += "com.twitter.inject" % "inject-app_2.11" % versions.finatra % "test" classifier "tests"
libraryDependencies += "com.twitter.inject" % "inject-core_2.11" % versions.finatra % "test" classifier "tests"
libraryDependencies += "com.twitter.inject" % "inject-modules_2.11" % versions.finatra % "test" classifier "tests"
libraryDependencies += "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test" classifier "tests"
libraryDependencies += "com.twitter.finatra" % "finatra-jackson_2.11" % versions.finatra % "test" classifier "tests"

libraryDependencies += "org.scalatest" %% "scalatest" % versions.scalatest % "test"
libraryDependencies += "org.specs2" %% "specs2" % versions.specs2 % "test"

// http://mvnrepository.com/artifact/com.google.code.gson/gson
libraryDependencies += "com.google.code.gson" % "gson" % "2.6.2"

// MongoDB Scala Driver
// http://mongodb.github.io/mongo-scala-driver/1.0/getting-started/installation-guide/
// http://mvnrepository.com/artifact/org.mongodb.scala/mongo-scala-driver_2.11
//libraryDependencies += "org.mongodb.scala" % "mongo-scala-driver_2.11" % "1.1.1"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % versions.mongo_driver
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % versions.mongo_driver % "test"


// typesafe config for scala wrapper: config-annotation
// ----------------------------------------------------
libraryDependencies += "com.wacai" %% "config-annotation" % "0.3.1"

libraryDependencies += "com.wacai" %% "config-annotation" % "0.3.1" % "test"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

scalacOptions += s"-Xmacro-settings:conf.output.dir=${baseDirectory.value / "conf"}"

unmanagedClasspath in Runtime += baseDirectory.value / "conf"
// ----------------------------------------------------