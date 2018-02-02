package algo1_worksheet

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import scala.io.Source
import scalaz._
import Scalaz._

object test extends App {
  //emplacement des fichiers sources
  var tweet_path:String="./tweets.txt"
  //Formatage des fichiers sources

  val sc = new SparkContext(new SparkConf().setAppName("SparkSenti"))
  val tweetsList = sc.textFile(tweet_path).collect.toArray
  val dico=Dictionary.dictionary
  //Dictionary.dictionary.foreach(println)
  //val dico=dictionary

  //Pour test sur petit Dico
  //val dico:Map[String,(Double,Double)]=Map("cool"->(0.2,2),"prout"->(0,1))

  //Affichage du resultat (premier tweet du fichier source + positif/negatif
  println(analysis(tweetsList,dico)(1)(0),analysis(tweetsList,dico)(1)(1))

  //Observe le score et ajoute le tweet et le resultat dans un Array
  def analysis(tweets:Array[String],dico:Map[(String, Char), List[(Double,Double)]]) : Array[Array[String]] = {
    var output=Array(Array("",""))
    var n=tweets.size
    for (tweet<-tweets) {
      var score = get_score_words(split_word(tweets(0)))
      if (score > 0.0) {
        output+:= Array(tweet, "positif")
      }
      else {
        if (score < 0.0) {
          output+:=Array(tweet, "negatif")
        }
        else {
          output+:= Array(tweet, "neutre")
        }
      }
    }
    return output
  }

  //Coupe la phrases en mots=succession de caracteres alphanumerique. Attention aux accents et autres.
  def split_word(sentence:String):Array[String]={
    return sentence.toLowerCase.split("\\W+")
  }

  //Calcul le score d'une liste de mot
  def get_score_words(words:Array[String]):Double={
    var sum=0.0
    for (word<-words) {
      if (dico.keySet.exists(key => (key._1, key._2) == (word, 'a'))) {
        sum=sum + dico((word, 'a')).foldLeft(0: Double){ case (acc, elem) => acc + (elem._1 - elem._2) }
      }

    }

    return sum
  }

}

//Partie Formatage Dico : Richard

object Dictionary {
  val dico_path = "./SentiWordNet.txt"
  val wordPosition = 4
  val negScorePosition = 3
  val posScorePosition = 2
  val typePosition = 0

  def dictionary: Map[(String, Char), List[(Double, Double)]] =
    scala.io.Source.fromFile(dico_path).getLines.toList
      .foldLeft(Map(): Map[(String, Char), List[(Double, Double)]])
        { case (map, line) => if (List('#', '\t').contains(line.head)) map else map |+| getMap(line)}

  def getMap(line: String): Map[(String, Char), List[(Double, Double)]] = {
    val cells = line.split("\t")

    cells(wordPosition)
      .split("#[0-9]* ?")
      .foldLeft(Map(): Map[(String, Char), List[(Double, Double)]])
        { case (map, word) => map |+| Map((word, cells(typePosition).head) -> List((cells(posScorePosition).toDouble, cells(negScorePosition).toDouble))) }
  }
}
