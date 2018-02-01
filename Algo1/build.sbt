import deployssh.DeploySSH._

lazy val root = (project in file(".")).
    enablePlugins(DeploySSH).
    settings(
        inThisBuild(List(
            organization := "AmigoCap",
            scalaVersion := "2.11.8",
            version := "0.1.0",
        )),
        name := "Algo1",
        libraryDependencies ++= Seq(
            "org.scalaz" %% "scalaz-core" % "7.2.18",
            "org.apache.spark" %% "spark-core" % "2.2.1" % "provided",
            "org.apache.spark" %% "spark-yarn" % "2.2.1" % "provided"
        ),
        autoScalaLibrary := false,
        packExcludeJars := Seq("scala-library-.*\\.jar"),
        deployResourceConfigFiles ++= Seq("server.conf"),
        deployArtifacts ++= Seq(
            ArtifactSSH(file("./tweets.txt"), "sparksenti")
        )
    )
