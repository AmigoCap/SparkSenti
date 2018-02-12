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
  val tweetsList = sc.textFile(tweet_path).map(elem => (Json.parse(elem) \ "text").as[String])
  val dico = Dictionary.dictionary

  //Affichage du resultat
  val analysis1 = analysis(tweetsList, dico)
  analysis1.collect().foreach(println)
  //println(analysis2(tweetsList))
  println("\nDo you wish to make a performance test based on a sample ? (y/n)")
  val yesno = scala.io.Source.fromInputStream(System.in).bufferedReader().readLine()
  println("ok let's go")
  if (yesno == "y"){
	val nbTot = analysis1.count()
	var opinion = ""
	var neg_neg = 0
	var neg_neu = 0
	var neg_pos = 0
	var neu_neg = 0
	var neu_neu = 0
	var neu_pos = 0
	var pos_neg = 0
	var pos_neu = 0
  	var pos_pos = 0
	val sample = analysis1.takeSample(false, (nbTot/10).toInt, System.nanoTime.toInt)
	sample
		.foreach{
			case (tweet,result) =>	{
				println(result)
				println("Is the meaning of this tweet positive, negative or neutral (pos/neg/neu) ?")
				opinion = scala.io.StdIn.readLine()
				(opinion,result) match {
					case ("neg","neg")=> neg_neg += 1
					case ("neg","neu")=> neg_neu += 1
					case ("neg","pos")=> neg_pos += 1	
					case ("neu","neg")=> neu_neg += 1	
					case ("neu","neu")=> neu_neu += 1	
					case ("neu","pos")=> neu_pos += 1	
					case ("pos","neg")=> pos_neg += 1	
					case ("pos","neu")=> pos_neu += 1	
					case ("pos","pos")=> pos_pos += 1						
				}		
			}
		} 	
	println(s"     pos     neu     neg\npos ${(pos_pos*nbTot/100).ToInt} ${(pos_neu*nbTot/100).ToInt} ${pos_neg*nbTot/100).ToInt} \nneu ${(neu_pos*nbTot/100).ToInt} ${(neu_neu*nbTot/100).ToInt} ${neu_neg*nbTot/100).ToInt}\nneg${(neg_pos*nbTot/100).ToInt ${(neg_neu*nbTot/100).ToInt} ${(neg_neg*nbTot/100).ToInt)}")
	
  }
  

  //Observe le score
  def analysis(tweets: RDD[String], dico: Dictionary.MapWordScore): RDD[(String,String)] =
    tweets
      .map { case (tweet) => (tweet, get_sentiment(get_score_words(corenlp.extract(tweet), dico))) }

  def analysis2(tweets: RDD[String]): String =
    tweets
        .map { case (tweet) => (tweet, corenlp.get_score(tweet)) }
        .aggregate("\n")(
          { case (output, (tweet: String, score: Int)) => s"${output}Tweet: ${tweet}\nScore: ${get_sentiment2(score)}\n\n" },
          { _ ++ _ }
        )

  def get_sentiment2(score: Int): String = score match {
    case 0 => "Très négatif"
    case 1 => "Négatif"
    case 2 => "Neutre"
    case 3 => "Positif"
    case 4 => "Très positif"
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