import sbtrelease.ReleaseStateTransformations.{checkSnapshotDependencies, commitNextVersion, commitReleaseVersion, inquireVersions, pushChanges, runClean, runTest, setNextVersion, setReleaseVersion, tagRelease}

name := "configuration"

organization := "com.gu"

licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
homepage := Some(url("https://github.com/guardian/guardian-configuration"))
scmInfo := Some(ScmInfo(
  url("https://github.com/guardian/guardian-configuration"),
  "scm:git@github.com:guardian/guardian-configuration.git"
))
developers := List(
  Developer(id="guardian", name="Guardian", email="", url=url("https://github.com/guardian")),
)
releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  releaseStepCommandAndRemaining("+test"),
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)
scalaVersion in ThisBuild := "2.12.2"

crossVersion := CrossVersion.binary

crossScalaVersions ++= Seq("2.11.11", "2.12.2")

ivyXML :=
    <dependencies>
        <exclude module="log4j"/>
    </dependencies>

resolvers ++= Seq(
  "Scala Tools Repository" at "https://scala-tools.org/repo-releases",
  "Guardian GitHub" at "https://guardian.github.com/maven/repo-releases"
)

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "1.4",
  "commons-lang" % "commons-lang" % "2.4",
  "org.apache.commons" % "commons-vfs2" % "2.1",
  "org.slf4j" % "slf4j-api" % "1.6.1"
)

libraryDependencies ++= Seq(
  "org.mockito" % "mockito-all" % "1.8.5" % "test",
  "org.slf4j" % "slf4j-simple" % "1.6.1" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
publishTo := sonatypePublishTo.value

maxErrors := 20

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalacOptions += "-deprecation"


