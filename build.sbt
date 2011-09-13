name := "guardian-configuration"

version := "3.1-SNAPSHOT"

organization := "com.gu.conf"

scalaVersion := "2.9.1"

crossScalaVersions ++= Seq("2.9.0-1", "2.8.1")

seq(ScalariformPlugin.settings: _*)

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
  "org.mockito" % "mockito-all" % "1.8.5" % "test",
  "org.slf4j" % "slf4j-simple" % "1.6.1" % "test"
)

libraryDependencies <<= (scalaVersion, libraryDependencies) { (sv, deps) =>
    val scalaTestVersion = sv match {
       case "2.8.1" => "1.5.1"
       case "2.9.0-1" => "1.6.1"
       case "2.9.1" => "1.6.1"
       case _ => error("Unsupported Scala version " + sv)
    }
    deps :+ ("org.scalatest" %% "scalatest" % scalaTestVersion % "test")
}


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


