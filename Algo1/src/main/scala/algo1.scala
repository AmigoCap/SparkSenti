package algo1_worksheet

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD

import scala.io.Source
import scalaz._
import Scalaz._

import play.api.libs.json._

import java.util.Properties
import edu.stanford.nlp.pipeline._
import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.rnn.RNNCoreAnnotations
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations._

import scala.collection.JavaConversions._

object corenlp {
  val props = new Properties()
  props.setProperty("annotators", "tokenize, ssplit, parse, lemma, sentiment")
  val pipeline: StanfordCoreNLP = new StanfordCoreNLP(props)
  val verb = "(V.*)".r
  val adjective = "(J.*)".r
  val noun = "(N.*)".r
  val adverb = "(RB.*)".r

  def extract(text: String): Array[(String, Char)] = {
    val doc: Annotation = new Annotation(text.toLowerCase)
    pipeline.annotate(doc)
    val sentences = doc.get(classOf[SentencesAnnotation])

    sentences
      .flatMap { sentence => sentence.get(classOf[TokensAnnotation])
        .map { token =>
          val lemma = token.get(classOf[LemmaAnnotation])
          token.get(classOf[PartOfSpeechAnnotation]) match {
            case verb(_*)      => (lemma, 'v')
            case noun(_*)      => (lemma, 'n')
            case adjective(_*) => (lemma, 'a')
            case adverb(_*)    => (lemma, 'r')
            case _             => ("", '_')
          }
        }
      }
      .toArray
  }

  def get_score(text: String): Int = {
    val doc: Annotation = new Annotation(text.toLowerCase)
    pipeline.annotate(doc)
    val sentences = doc.get(classOf[SentencesAnnotation])

    math.round(sentences
      .map { sentence => RNNCoreAnnotations.getPredictedClass(sentence.get(classOf[AnnotatedTree])) }
      .sum / sentences.length
    ).toInt
  }
}

object test extends App {
  //emplacement des fichiers sources
  var tweet_path = args(0)
  //Formatage des fichiers sources

  val sc = new SparkContext(new SparkConf().setAppName("SparkSenti"))

  val tweetAnalysis = sc.textFile(tweet_path).map(elem => "{'result': '" + get_sentiment2(corenlp.get_score(((Json.parse(elem) \ "full_text").as[String])))    + "', 'tweet': " + elem  + "}")
  tweetAnalysis.collect().foreach(println)
  
  //val dico = Dictionary.dictionary

  //Affichage du resultat
  //println(analysis(tweetsList, dico))
  //println(analysis2(tweetsList))
  //tweetsList.take(3).foreach(println)
	
  //           get_sentiment(corenlp.get_score(Json.parse(elem) \ "full_text").as[String]._1))
  //Observe le score
  /*def analysis(tweets: RDD[String], dico: Dictionary.MapWordScore): String = {
    tweets
      .map { case (tweet) => (tweet, get_score_words(corenlp.extract(tweet), dico)) }
      .aggregate("\n")(
        { case (output, (tweet: String, score: Double)) => s"${output}Tweet: ${tweet}\nScore: ${get_sentiment(score)}\n\n" },
        { _ ++ _ }
      )
  }*/

  /*def analysis2(tweets: RDD[(String,String)]): String =
	tweets
		.map { case (tweet) => (tweet._1, corenlp.get_score(tweet._1),tweet._2) }
		.aggregate("\n")(
		  { case (output, (tweet: String, score: Int, id: String)) => s"${output}Tweet: ${tweet}\nScore: ${get_sentiment2(score)}\nTweetID: ${id}\n\n" },
		  { _ ++ _ }
		)
	*/

		
		
	/*def analysis3(tweets: RDD[(String,String)]: String = {
	tweets
		.map { case (tweet) => (tweet.-1, corenlp.get_score(tweet.-1),tweet.-2) }
		.aggregate("\n")(
		  { case (output, (tweet: String, score: Int, user: String)) => s"${output}${user} tweeted: ${tweet}\nScore: ${get_sentiment2(score)}\n\n" },
		  { _ ++ _ }
		)
	}*/

  def get_sentiment2(score: Int): String = score match {
    case 0 => "Tres negatif"
    case 1 => "Negatif"
    case 2 => "Neutre"
    case 3 => "Positif"
    case 4 => "Tres positif"
  }

  def get_sentiment(score: Double): String = score match {
    case x if (x < 0) => "Négatif"
    case 0 => "Neutre"
    case _ => "Positif"
  }

  //Calcul le score d'une liste de mot
  def get_score_words(wordsType: Array[(String, Char)], dico: Dictionary.MapWordScore): Double = {
    wordsType
      .filter { wordType => dico.keySet.exists({ key => (key._1, key._2) == wordType }) }
      .map { wordType => dico(wordType).foldLeft(0: Double) { case (acc, elem) => acc + (elem._1 - elem._2) } }
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
