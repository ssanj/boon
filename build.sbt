lazy val scala212 = "2.12.13"

lazy val scala213 = "2.13.4"

lazy val supportedScalaVersions = List(scala213, scala212)

crossScalaVersions := supportedScalaVersions

ThisBuild / scalaVersion := scala213

lazy val commonSettings = Seq(
  organization := "net.ssanj",
  version := "1.1.0",
  licenses ++= Seq(("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))),
  scalacOptions ++= Seq(
                      "-encoding", "utf-8",
                      "-unchecked",
                      "-deprecation",
                      "-explaintypes",
                      "-feature",
                      "-Xfatal-warnings",
                      "-Xlint:_",
                      "-Ywarn-dead-code",
                      "-language:implicitConversions",
                      "-language:higherKinds"
                    ),

  scalacOptions in (Compile, console) := Seq(
                      "-encoding", "utf-8",
                      "-unchecked",
                      "-deprecation",
                      "-explaintypes",
                      "-feature",
                      "-language:implicitConversions",
                      "-language:higherKinds"
  ),

  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value
)

lazy val scalaReflect = Def.setting { "org.scala-lang" % "scala-reflect" % scalaVersion.value }

lazy val scalaCheckVersion = "1.15.2"

lazy val boon = (project in file("core"))
  .dependsOn(boonMacro)
  .settings(
    commonSettings,
    name := "boon",
    testFrameworks := Seq(new TestFramework("boon.sbt.BoonFramework"), sbt.TestFrameworks.ScalaCheck),
    libraryDependencies ++= Seq(
        "org.scala-sbt"  % "test-interface" % "1.0",
        "org.scalacheck" %% "scalacheck"    % scalaCheckVersion % Test
    ),
    crossScalaVersions := supportedScalaVersions
  )


lazy val boonLaws = (project in file("laws"))
  .dependsOn(boon % "test->test;compile->compile")
  .settings(
    commonSettings,
    name := "boon-laws",
    testFrameworks := Seq(sbt.TestFrameworks.ScalaCheck),
    libraryDependencies ++= Seq(
        "org.scalacheck" %% "scalacheck" % scalaCheckVersion
    ),
    crossScalaVersions := supportedScalaVersions
  )

lazy val boonMacro = (project in file("macro"))
  .settings(
    commonSettings,
    name := "boon-macro",
    libraryDependencies ++= Seq(
      scalaReflect.value
    ),
    crossScalaVersions := supportedScalaVersions
)

lazy val boonProj = (project in file(".")).
  settings(
    commonSettings,
    name := "boon-project",
    crossScalaVersions := supportedScalaVersions
  ).aggregate(boonMacro, boon, boonLaws)
