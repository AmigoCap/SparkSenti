package algo1_worksheet

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD

import scala.io.Source
import scalaz._
import Scalaz._

import play.api.libs.json._

object test extends App {
  //emplacement des fichiers sources
  var tweet_path = args(0)
  //Formatage des fichiers sources

  val sc = new SparkContext(new SparkConf().setAppName("SparkSenti"))
  val tweetsList = sc.textFile(tweet_path).map(elem => (Json.parse(elem) \ "text").as[String])
  val dico = Dictionary.dictionary

  //Affichage du resultat
  println(analysis(tweetsList, dico))

  //Observe le score
  def analysis(tweets: RDD[String], dico: Dictionary.MapWordScore): String = {
    tweets
      .map { case (tweet) => (tweet, get_score_words(tweet.toLowerCase.split("\\W+"), dico)) }
      .aggregate("\n")(
        { case (output, (tweet: String, score: Double)) => s"${output}Tweet: ${tweet}\nScore: ${get_sentiment(score)}\n\n" },
        { _ ++ _ }
      )
  }

  def get_sentiment(score: Double): String = score match {
    case x if (x < 0) => "Négatif"
    case 0 => "Neutre"
    case _ => "Positif"
  }

  //Calcul le score d'une liste de mot
  def get_score_words(words: Array[String], dico: Dictionary.MapWordScore): Double = {
    words
      .filter { word => dico.keySet.exists({ key => (key._1, key._2) == (word, 'a') }) }
      .map { word => dico((word, 'a')).foldLeft(0: Double) { case (acc, elem) => acc + (elem._1 - elem._2) } }
      .foldLeft(0: Double)(_+_)
  }
}

object Dictionary {
  type MapWordScore = Map[(String, Char), List[(Double, Double)]]
  val dico_path = "SentiWordNet.txt"
  val wordPosition = 4
  val negScorePosition = 3
  val posScorePosition = 2
  val typePosition = 0

  def dictionary: MapWordScore =
    scala.io.Source.fromFile(dico_path).getLines.toList
      .foldLeft(Map(): Map[(String, Char), List[(Double, Double)]])
        { case (map, line) => if (List('#', '\t').contains(line.head)) map else map |+| getMap(line)}

  def getMap(line: String): MapWordScore = {
    val cells = line.split("\t")

    cells(wordPosition)
      .split("#[0-9]* ?")
      .foldLeft(Map(): MapWordScore)
        { case (map, word) => map |+| Map((word, cells(typePosition).head) -> List((cells(posScorePosition).toDouble, cells(negScorePosition).toDouble))) }
  }
}
