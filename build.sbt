import Dependencies.Versions
import org.jmotor.sbt.plugin.ComponentSorter
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

name := "scala-utils"

organization := "org.jmotor"

enablePlugins(Dependencies, Publishing)

scalaVersion := Versions.scala213

crossScalaVersions := Seq(Versions.scala212, Versions.scala213)

dependencyUpgradeModuleNames := Map(
  "scala-library" -> "scala",
  "undertow-.*" -> "undertow"
)

dependencyUpgradeComponentSorter := ComponentSorter.ByAlphabetically

releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  pushChanges
)
