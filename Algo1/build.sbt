lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "",
      scalaVersion := "2.11.8",
      version      := "0.1.0"
    )),
    name := "Algo1",
    libraryDependencies ++= Seq(
        "org.scalaz" %% "scalaz-core" % "7.2.18",
        "org.apache.spark" %% "spark-core" % "1.2.1"
    ),
    test in assembly := {},
    mainClass in assembly := Some("algo1_worksheet.test"),
    assemblyMergeStrategy in assembly := {
        case PathList("META-INF", xs @ _*) => MergeStrategy.discard
        case _ => MergeStrategy.first
    }
  )
