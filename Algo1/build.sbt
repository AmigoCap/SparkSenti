import scala.io.Source
import scala.sys.process._

val push = taskKey[Unit]("Package, compress and send to server")
val submit = taskKey[Unit]("Decompress and submit spark job")

val getCredentials = {
    val regexp = "(.*)=(.*)".r
    val parameters = Source.fromFile("server.conf")
        .getLines
        .foldLeft(Map(): Map[String, String])
            { case (map, regexp(parameterName, parameterValue)) =>  map + (parameterName -> parameterValue) }
    val server = s"""${parameters("user")}@${parameters("host")}"""
    val password = parameters("password")
    (server, password)
}

push := {
    val archives = packArchive.value
    val gzArchive = archives.head
    val (server, password) = getCredentials

    s"./push.sh ${gzArchive} ${server} ${password}" !
}

submit := {
    val (server, password) = getCredentials
    val fileName = "SparkSenti-0.1"

    s"./submit.sh ${fileName} ${server} ${password}" !
}

lazy val root = (project in file(".")).
    settings(
        inThisBuild(List(
            organization := "AmigoCap",
            scalaVersion := "2.11.8",
            version := "0.1",
        )),
        name := "SparkSenti",
        libraryDependencies ++= Seq(
            "org.scalaz" %% "scalaz-core" % "7.2.18",
            "org.apache.spark" %% "spark-core" % "2.2.1" % "provided",
            "org.apache.spark" %% "spark-yarn" % "2.2.1" % "provided"
        ),
        autoScalaLibrary := false,
        packExcludeJars := Seq("scala-library-.*\\.jar")
    )
