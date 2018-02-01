enablePlugins(PackPlugin)

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "AmigoCap",
      scalaVersion := "2.11.8",
      version      := "0.1.0"
    )),
    name := "Algo1",
    libraryDependencies ++= Seq(
        "org.scalaz" %% "scalaz-core" % "7.2.18",
        "org.apache.spark" %% "spark-core" % "1.2.1" % "provided"
    ),
    autoScalaLibrary := false,
    packExcludeJars := Seq("scala-library-.*\\.jar"),
  )
