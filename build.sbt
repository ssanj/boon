lazy val commonSettings = Seq(
  scalaVersion := "2.12.8",
  organization := "net.ssanj",
  version := "0.0.1-b18",
  scalacOptions ++= Seq(
                      "-unchecked",
                      "-deprecation",
                      "-feature",
                      "-Xfatal-warnings",
                      "-Xlint:_",
                      "-Ywarn-dead-code",
                      "-Ywarn-inaccessible",
                      "-Ywarn-unused-import",
                      "-Ywarn-infer-any",
                      "-Ywarn-nullary-override",
                      "-Ywarn-nullary-unit",
                      "-language:implicitConversions"
                     )
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
        "org.scalacheck" %% "scalacheck"    % "1.13.4" % "test"
    )
  )

lazy val boonMacro = (project in file("macro"))
  .settings(
    commonSettings,
    licenses ++= Seq(("Apache2", url("https://opensource.org/licenses/Apache-2.0"))),
    name := "boon-macro",
    libraryDependencies ++= Seq(
    scalaReflect.value
    // "org.scalatest"  %% "scalatest"   % "3.0.1"  % "test"
  )
)

lazy val boonProj = (project in file(".")).
  settings(
    commonSettings,
    name := "boon-project",
  ).aggregate(boonMacro, boon)


// scalacOptions in (Compile, console) --= Seq("-Xfatal-warnings", "-Ywarn-unused-import")

// scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value
