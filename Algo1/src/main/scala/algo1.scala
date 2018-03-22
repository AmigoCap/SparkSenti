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
  var tweet_path = args(0)
  val sc = new SparkContext(new SparkConf().setAppName("SparkSenti"))

  val tweetAnalysis = sc.textFile(tweet_path)
    .map    { elem => (elem, corenlp.get_score(((Json.parse(elem) \ "full_text").as[String]))) }
    .filter { case (elem, score) => score != 0 && score != 4 }
    .map    { case (elem, score) => s"""{"result": "${get_sentiment(score)}", "tweet": ${elem}}""" }
  tweetAnalysis.collect().foreach(println)

  def get_sentiment(score: Int): String = score match {
    case 1 => "Negatif"
    case 2 => "Neutre"
    case 3 => "Positif"
  }
}
