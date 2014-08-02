name := "configuration"

organization := "com.gu"

scalaVersion in ThisBuild := "2.11.2"

crossVersion := CrossVersion.binary

crossScalaVersions ++= Seq("2.10.4", "2.11.2")

releaseSettings

ivyXML :=
    <dependencies>
        <exclude module="log4j"/>
    </dependencies>

resolvers ++= Seq(
  "Scala Tools Repository" at "http://scala-tools.org/repo-releases",
  "Guardian GitHub" at "http://guardian.github.com/maven/repo-releases"
)

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "1.4",
  "commons-lang" % "commons-lang" % "2.4",
  "org.apache.commons" % "commons-vfs2" % "2.0",
  "org.slf4j" % "slf4j-api" % "1.6.1"
)

libraryDependencies ++= Seq(
  "org.mockito" % "mockito-all" % "1.8.5" % "test",
  "org.slf4j" % "slf4j-simple" % "1.6.1" % "test",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
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


