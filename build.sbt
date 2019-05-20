scalaVersion := "2.12.6"

libraryDependencies += "org.http4s" %% "http4s-core" % "0.20.0"
libraryDependencies += "org.http4s" %% "http4s-server" % "0.20.0"
libraryDependencies += "org.http4s" %% "http4s-dsl" % "0.20.0"
libraryDependencies += "org.http4s" %% "http4s-blaze-server" % "0.20.0"

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.0")

