lazy val kumoshi = project
  .in(file("."))
  .aggregate(cli, core)
  .settings(
    commonSettings,
    // root project doesn't produce stuff itself.
    Compile / packageBin := { file("") },
    Compile / packageSrc := { file("") },
    Compile / packageDoc := { file("") }
  )

lazy val cli = project
  .in(file("bin/cli"))
  .dependsOn(core)
  .settings(
    commonSettings,
    libraryDependencies ++=
      dependencies.decline ++
        dependencies.cats ++
        dependencies.zio
  )

lazy val core = project
  .in(file("lib/core"))
  .settings(
    commonSettings
  )

lazy val commonSettings = Seq(
  run / connectInput := true,
  fork := true,
  Test / parallelExecution := true,
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-unchecked",
    "-deprecation",
    "-feature",
    "-explaintypes",
    "-Ywarn-numeric-widen",
    "-Yrangepos",
    "-Ywarn-unused:imports",
    "-Ywarn-value-discard",
    "-Ywarn-dead-code",
    "-Xlint:adapted-args",
    "-Xlint:infer-any",
    "-Xlint:missing-interpolator",
    "-Xlint:private-shadow",
    "-Xlint:type-parameter-shadow"
  ),
  // some simple / saner scala project layout
  Compile / scalaSource := baseDirectory.value / "src",
  Test / scalaSource := baseDirectory.value / "test",
  target := baseDirectory.value / "target",
  sourcesInBase := false
)

lazy val dependencies =
  new {
    val decline = {
      val version = "1.0.0"

      Seq(
        "com.monovore" %% "decline" % version
      )
    }

    val cats = {
      val version = "2.1.1"

      Seq(
        "org.typelevel" %% "cats-core" % version
      )
    }

    val zio = {
      val version = "1.0.0-RC18-2"
      val catsInteropVersion = "2.0.0.0-RC12"

      Seq(
        "dev.zio" %% "zio" % version,
        "dev.zio" %% "zio-streams" % version,
        "dev.zio" %% "zio-interop-cats" % catsInteropVersion
      )
    }
  }

// sanity
Global / onChangedBuildSource := IgnoreSourceChanges // still keeps generating fucking target folder!!! fuck this.
Global / useSuperShell := false // really do not want supershell
Global / turbo := false // not sure if does anything...
Global / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat // not sure if this does anything
