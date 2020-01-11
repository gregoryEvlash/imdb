import com.imdb.Dependencies._

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(organization := "com.lunatech", scalaVersion := "2.12.9")),
    name := "imdb",
    version := "0.0.1",
    libraryDependencies ++= all,
    parallelExecution in Test := false

  )

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true, includeDependency = true)