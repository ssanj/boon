ThisBuild / organization := "net.ssanj"
ThisBuild / organizationName := "Sanj's Github Projects"
ThisBuild / organizationHomepage := Some(url("https://github.com/ssanj"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/ssanj/boon"),
    "scm:git@github.com:ssanj/boon.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "ssahayam",
    name  = "sanjiv sahayam",
    email = "sanjsmailbox@gmail.com",
    url   = url("https://github.com/ssanj")
  )
)

ThisBuild / description := "A small framework for testing pure code"
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/ssanj/boon"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true