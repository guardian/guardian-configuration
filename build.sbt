import sbtrelease.ReleaseStateTransformations.{checkSnapshotDependencies, commitNextVersion, commitReleaseVersion, inquireVersions, runClean, runTest, setNextVersion, setReleaseVersion, tagRelease}
import sbtversionpolicy.withsbtrelease.ReleaseVersion

name := "configuration"

organization := "com.gu"

licenses := Seq(License.Apache2)

releaseCrossBuild := true
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion
)
releaseVersion := ReleaseVersion.fromAggregatedAssessedCompatibilityWithLatestRelease().value

scalaVersion := "2.12.19"

crossScalaVersions ++= Seq(scalaVersion.value)

ivyXML :=
    <dependencies>
        <exclude module="log4j"/>
    </dependencies>

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "1.4",
  "commons-lang" % "commons-lang" % "2.4",
  "org.apache.commons" % "commons-vfs2" % "2.1",
  "org.slf4j" % "slf4j-api" % "1.6.1",
  "org.scalatestplus" %% "mockito-5-10" % "3.2.18.0" % Test,
  "org.slf4j" % "slf4j-simple" % "1.6.1" % Test,
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)

maxErrors := 20

scalacOptions ++= Seq("-deprecation", "-release:8")

Test / testOptions +=
  Tests.Argument(TestFrameworks.ScalaTest, "-u", s"test-results/scala-${scalaVersion.value}", "-o")
