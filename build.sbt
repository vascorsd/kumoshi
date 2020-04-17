lazy val kumoshi = project
  .in(file("."))
  .aggregate(cli, lib)
  .settings(
    commonSettings,

    // root project doesn't produce stuff itself.
    Compile / packageBin := { file("") },
    Compile / packageSrc := { file("") },
    Compile / packageDoc := { file("") },
  )

lazy val cli = project
  .dependsOn(lib)
  .settings(
    commonSettings,
    libraryDependencies ++=
      dependencies.decline ++ dependencies.cats
  )

lazy val lib = project
  .settings(
    commonSettings
  )

lazy val commonSettings = Seq(
  run / connectInput := true,
  fork := true,
  Test / parallelExecution := true,
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-language:higherKinds",
    "-encoding",
    "utf8",
    "-Xlint"
  ),
  // some simple / saner scala project layout
  Compile / scalaSource := baseDirectory.value / "src",
  Test    / scalaSource := baseDirectory.value / "test",
  target                := baseDirectory.value / ".target",
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
  }

// sanity
Global / onChangedBuildSource := IgnoreSourceChanges // still keeps generating fucking target folder!!! fuck this.
Global / useSuperShell := false // really do not want supershell
Global / turbo := false // not sure if does anything...
Global / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat  // not sure if this does anything
Global / bloopExportJarClassifiers := Some(Set("sources"))
