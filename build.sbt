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

scalaVersion := "2.13.14"

ivyXML :=
    <dependencies>
        <exclude module="log4j"/>
    </dependencies>

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.16.1",
  "commons-lang" % "commons-lang" % "2.6",
  "org.apache.commons" % "commons-vfs2" % "2.9.0" exclude("org.apache.hadoop", "hadoop-hdfs-client"),
  "org.slf4j" % "slf4j-api" % "1.7.36",
  "org.scalatestplus" %% "mockito-5-10" % "3.2.18.0" % Test,
  "org.slf4j" % "slf4j-simple" % "1.7.36" % Test,
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)

maxErrors := 20

scalacOptions ++= Seq("-deprecation", "-release:8")

Test / testOptions +=
  Tests.Argument(TestFrameworks.ScalaTest, "-u", s"test-results/scala-${scalaVersion.value}", "-o")
