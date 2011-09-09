name := "guardian-configuration"

version := "3.0-SNAPSHOT"

organization := "com.gu.conf"

scalaVersion := "2.8.1"

crossScalaVersions ++= Seq("2.9.0-1", "2.9.1")

resolvers ++= Seq(
  "Scala Tools Repository" at "http://scala-tools.org/repo-releases",
  "Guardian GitHub" at "http://guardian.github.com/maven/repo-releases"
)

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "1.4",
  "commons-lang" % "commons-lang" % "2.4",
  "org.slf4j" % "slf4j-api" % "1.6.1"
)

libraryDependencies ++= Seq(
  "org.hamcrest" % "hamcrest-all" % "1.1" % "test",
  "org.mockito" % "mockito-all" % "1.8.5" % "test",
  "junit" % "junit" % "4.8.2" % "test",
  "com.google.collections" % "google-collections" % "1.0" % "test",
  "org.slf4j" % "slf4j-simple" % "1.6.1" % "test",
  "com.novocode" % "junit-interface" % "0.6" %  "test->default"
)

publishTo <<= (version) { version: String =>
    val publishType = if (version.endsWith("SNAPSHOT")) "snapshots" else "releases"
    Some(
        Resolver.file(
            "guardian github " + publishType,
            file(System.getProperty("user.home") + "/guardian.github.com/maven/repo-" + publishType)
        )
    )
}

maxErrors := 20

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

scalacOptions += "-deprecation"


