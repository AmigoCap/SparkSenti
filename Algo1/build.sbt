import scala.io.Source
import scala.sys.process._
import java.io._

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
            "com.typesafe.play" %% "play-json" % "2.6.7",
            "org.apache.spark" %% "spark-core" % "2.2.1" % "provided",
            "org.apache.spark" %% "spark-yarn" % "2.2.1" % "provided",
            "edu.stanford.nlp" % "stanford-corenlp" % "3.3.0" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp"))
        ),
        autoScalaLibrary := false,
        packExcludeJars := Seq("scala-library-.*\\.jar")
    )

val init = taskKey[Unit]("Initialize the server.conf file")
val push = taskKey[Unit]("Send only application jar to server")
val pushAll = taskKey[Unit]("Package, compress and send to server every needed files")
val submit = inputKey[Unit]("Decompress and submit spark job")
val put = inputKey[Unit]("Send a file to the remote server and put it into hdfs")

def getCredentials = {
    val regexp = "(.*)=(.*)".r
    val parameters = Source.fromFile("server.conf").getLines
        .foldLeft(Map(): Map[String, String])
            { case (map, regexp(parameterName, parameterValue)) =>  map + (parameterName -> parameterValue) }
    val server = s"""${parameters("user")}@${parameters("host")}"""
    val password = parameters("password")
    (server, password)
}

push := {
    val source = pack.value
    val (server, password) = getCredentials
    val directory = "SparkSenti-0.1/lib"
    val file = "target/scala-2.11/sparksenti_2.11-0.1.jar"

    s"./scripts/push.sh ${file} ${server} ${password} ${directory}" !
}

put := {
    val fileName = Def.spaceDelimited("<arg>").parsed.head
    val (server, password) = getCredentials

    s"./scripts/put.sh ${fileName} ${server} ${password}" !
}

pushAll := {
    val archives = packArchive.value
    val gzArchive = archives.head
    val (server, password) = getCredentials

    s"./scripts/push-all.sh ${gzArchive} ${server} ${password}" !
}

submit := {
    val fileName = Def.spaceDelimited("<arg>").parsed.head
    val (server, password) = getCredentials
    val directory = "SparkSenti-0.1"

    s"./scripts/submit.sh ${directory} ${server} ${password} ${fileName}" !
}

init := {
    if (!new File("server.conf").exists) {
        val file = new File("server.conf")
        val bw = new BufferedWriter(new FileWriter(file))

        val parameters = Source.fromFile("server.conf.dist").getLines.toList
        parameters
            .map { case (elem) =>
                println(s"Enter value for parameter ${elem}: ")
                val input = scala.io.Source.fromInputStream(System.in).bufferedReader().readLine
                bw.write(s"${elem}=${input}")
                bw.newLine()
            }
        bw.close()
    }
}
