scalaVersion := "2.12.13"

//libraryDependencies += "org.http4s" %% "http4s-core" % "0.20.0"
//libraryDependencies += "org.http4s" %% "http4s-server" % "0.20.0"
//libraryDependencies += "org.http4s" %% "http4s-dsl" % "0.20.0"
//libraryDependencies += "org.http4s" %% "http4s-blaze-server" % "0.20.0"
libraryDependencies += "org.http4s" %% "http4s-core" % "0.21.24"
libraryDependencies += "org.http4s" %% "http4s-server" % "0.21.24"
libraryDependencies += "org.http4s" %% "http4s-dsl" % "0.21.24"
libraryDependencies += "org.http4s" %% "http4s-blaze-server" % "0.21.24"

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.0")

//addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.0" cross CrossVersion.full)
//
//// if your project uses both 2.10 and polymorphic lambdas
//libraryDependencies ++= (scalaBinaryVersion.value match {
//  case "2.10" =>
//    compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full) :: Nil
//  case _ =>
//    Nil
//})

