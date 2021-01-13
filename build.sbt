lazy val commonSettings = Seq(
  scalaVersion := "2.13.1",
  organization := "net.ssanj",
  version := "0.0.11-b01",
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
                      // "-Ypartial-unification",
                      // "-Ywarn-infer-any",
                      // "-Ywarn-inaccessible",
                      // "-Ywarn-unused:_",
                      // "-Yno-adapted-args",
                      // "-Ywarn-infer-any",
                      // "-Ywarn-nullary-override",
                      // "-Ywarn-nullary-unit",
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

lazy val boon = (project in file("core"))
  .dependsOn(boonMacro)
  .settings(
    commonSettings,
    name := "boon",
    testFrameworks := Seq(new TestFramework("boon.sbt.BoonFramework"), sbt.TestFrameworks.ScalaCheck),
    libraryDependencies ++= Seq(
        "org.scala-sbt"  % "test-interface" % "1.0",
        compilerPlugin("com.github.ghik" % "silencer-plugin" % "1.6.0" cross CrossVersion.full),
        "com.github.ghik" % "silencer-lib" % "1.6.0" % Provided cross CrossVersion.full,
        "org.scalacheck" %% "scalacheck"    % "1.15.2" % Test
    )
  )


lazy val boonLaws = (project in file("laws"))
  .dependsOn(boon % "test->test;compile->compile")
  .settings(
    commonSettings,
    name := "boon-laws",
    testFrameworks := Seq(sbt.TestFrameworks.ScalaCheck),
    libraryDependencies ++= Seq(
        "org.scalacheck" %% "scalacheck" % "1.15.2"
    )
  )

lazy val boonMacro = (project in file("macro"))
  .settings(
    commonSettings,
    name := "boon-macro",
    libraryDependencies ++= Seq(
    scalaReflect.value
  )
)

lazy val boonProj = (project in file(".")).
  settings(
    commonSettings,
    name := "boon-project",
  ).aggregate(boonMacro, boon, boonLaws)
