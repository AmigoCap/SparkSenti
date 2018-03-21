package twitterClassifier

import org.apache.spark.SparkContext
import org.apache.spark.mllib.clustering.KMeansModel
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.util.Properties
import edu.stanford.nlp.pipeline._
import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.rnn.RNNCoreAnnotations
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations._

import scala.collection.JavaConversions._


object Predict extends App {
  import SparkSetup._

  val options = PredictOptions.parse(args)
  val ssc = new StreamingContext(sc, Seconds(options.intervalInSecs))
  Predictor.doIt(options, sc, ssc)
}

object Predictor {
  def doIt(options: PredictOptions, sc: SparkContext, ssc: StreamingContext) {

    println("Materializing Twitter stream...")
    TwitterUtils.createStream(ssc, maybeTwitterAuth)
      .map(_.getText)
      .foreachRDD { rdd => println(rdd)
      }

    println("Initialization complete, starting streaming computation.")
    ssc.start()
    ssc.awaitTermination()
  }

  def get_sentiment2(score: Int): String = score match {
    case 0 => "Tres negatif"
    case 1 => "Negatif"
    case 2 => "Neutre"
    case 3 => "Positif"
    case 4 => "Tres positif"
  }

}


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
