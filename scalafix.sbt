// scalafix rules / dependencies

ThisBuild / scalafixScalaBinaryVersion := (ThisBuild / scalaBinaryVersion).value
ThisBuild / scalafixDependencies ++= Seq(
  "com.github.vovapolu"  %% "scaluzzi"         % "0.1.18",
  "com.github.liancheng" %% "organize-imports" % "0.5.0"
)
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
