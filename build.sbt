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
  .dependsOn(core, endpoints)
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

lazy val endpoints = project
  .in(file("lib/endpoints"))
  .settings(
    commonSettings,
    libraryDependencies ++=
      dependencies.software_mill_http
  )

lazy val commonSettings = Seq(
  run / connectInput := true,
  fork := true,
  Test / parallelExecution := true,
  scalacOptions ++= Seq(
    // most basic needed flags
    "-encoding",
    "UTF-8",
    "-unchecked",
    "-deprecation",
    "-feature",
    "-explaintypes",
    // magic ?
    "-Ymacro-annotations",
    "-Yrangepos",
    // warns
    "-Ywarn-numeric-widen",
    "-Ywarn-unused:imports",
    "-Ywarn-value-discard",
    "-Ywarn-dead-code",
    // lints
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
  Compile / doc / sources := Nil,
  sourcesInBase := false,
  // compiler plugins
  addCompilerPlugin(
    "io.tryp" % "splain" % "0.5.8" cross CrossVersion.patch
  ),
  addCompilerPlugin(
    "org.typelevel" %% "kind-projector" % "0.12.0" cross CrossVersion.full
  ),
  addCompilerPlugin(
    "com.olegpy" %% "better-monadic-for" % "0.3.1"
  ),
  addCompilerPlugin(
    scalafixSemanticdb
  )
)

// libraries dependencies
lazy val dependencies =
  new {
    val decline = {
      val version = "1.2.0"
      Seq(
        "com.monovore" %% "decline" % version
      )
    }

    val cats = {
      val coreVersion   = "2.1.1"
      val effectVersion = "2.1.3"
      Seq(
        "org.typelevel" %% "cats-core"   % coreVersion,
        "org.typelevel" %% "cats-effect" % effectVersion
      )
    }

    val zio = {
      val version            = "1.0.0-RC18-2"
      val catsInteropVersion = "2.0.0.0-RC12"
      Seq(
        "dev.zio" %% "zio"              % version,
        "dev.zio" %% "zio-streams"      % version,
        "dev.zio" %% "zio-interop-cats" % catsInteropVersion
      )
    }

    val derevo = {
      val version = "0.11.2"
      Seq(
        "org.manatki" %% "derevo-cats" % version
      )
    }

    val newType = {
      val version = "0.4.3"
      Seq(
        "io.estatico" %% "newtype" % version
      )
    }

    val software_mill_http = {
      val sttpVersion  = "2.0.9"
      val modelVersion = "1.0.3"
      val tapirVersion = "0.13.2"
      Seq(
        //sttp
        "com.softwaremill.sttp.client" %% "core" % sttpVersion,
        // model
        "com.softwaremill.sttp.model" %% "core" % modelVersion,
        // tapir
        "com.softwaremill.sttp.tapir" %% "tapir-core"        % tapirVersion,
        "com.softwaremill.sttp.tapir" %% "tapir-sttp-client" % tapirVersion
      )
    }
  }

// sanity
Global / onChangedBuildSource := ReloadOnSourceChanges // still keeps generating fucking target folder!!! fuck this.
Global / useSuperShell := false                        // really do not want supershell
Global / turbo := false                                // not sure if does anything...
//Global / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat // not sure if this does anything
