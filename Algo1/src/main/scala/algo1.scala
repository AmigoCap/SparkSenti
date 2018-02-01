package algo1_worksheet

import scala.io.Source
import scalaz._
import Scalaz._

object test extends App {
  //emplacement des fichiers sources
  var tweet_path:String="/Users/Frego/Documents/Centrale/4A/OPTION/08_Projet/Algo1/tweets.txt"
  //Formatage des fichiers sources
  val tweetsList = Source.fromFile(tweet_path).getLines.toArray
  val dico=Dictionary.dictionary
  //Dictionary.dictionary.foreach(println)
  //val dico=dictionary

  //Pour test sur petit Dico
  //val dico:Map[String,(Double,Double)]=Map("cool"->(0.2,2),"prout"->(0,1))

  //Affichage du resultat (premier tweet du fichier source + positif/negatif
  println(analysis(tweetsList,dico)(1)(0),analysis(tweetsList,dico)(1)(1))

  //Observe le score et ajoute le tweet et le resultat dans un Array
  def analysis(tweets:Array[String],dico:Map[String,(Double,Double)]) : Array[Array[String]] = {
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
      if (dico.keySet.exists(_ == word)) {
        sum=sum+dico(word)._1 - dico(word)._2
      }

    }

    return sum
  }

}

//Partie Formatage Dico : Richard

object Dictionary {
  val dico_path = "/Users/Frego/Documents/Centrale/4A/OPTION/08_Projet/Algo1/dico.txt"
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
