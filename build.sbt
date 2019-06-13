lazy val commonSettings = Seq(
  scalaVersion := "2.12.8",
  organization := "net.ssanj",
  version := "0.0.5-b01",
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
                      "-Ypartial-unification",
                      "-Ywarn-infer-any",
                      "-Ywarn-inaccessible",
                      "-Ywarn-unused:_",
                      "-Yno-adapted-args",
                      "-Ywarn-infer-any",
                      "-Ywarn-nullary-override",
                      "-Ywarn-nullary-unit",
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
        compilerPlugin("com.github.ghik" %% "silencer-plugin" % "1.4.1"),
        "com.github.ghik" %% "silencer-lib" % "1.4.1" % Provided,
        "org.scalacheck" %% "scalacheck"    % "1.14.0" % Test
    )
  )

lazy val boonMacro = (project in file("macro"))
  .settings(
    commonSettings,
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
