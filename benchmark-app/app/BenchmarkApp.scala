import com.johnsnowlabs.nlp.embeddings.{BertEmbeddings, RoBertaEmbeddings, XlmRoBertaEmbeddings}
import com.johnsnowlabs.nlp.training.CoNLL
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.{Dataset, SparkSession}

import java.util.logging.{Level, Logger}


object BenchmarkApp {

  private val logger: Logger = Logger.getLogger(this.getClass.toString)

  def main(args: Array[String]): Unit = {
    logger.setLevel(Level.INFO)

    val MODEL_PATH = sys.env.get("MODEL_PATH").orNull
    val DATA_PATH = sys.env.get("DATA_PATH").orNull
    val MODEL = sys.env.getOrElse("MODEL", "BERT")
    val BATCH_SIZE: Integer = sys.env.get("BATCH_SIZE").map(Integer.valueOf).getOrElse(8)
    val MAX_SENTENCE_LENGTH: Integer = sys.env.get("MAX_SENTENCE_LENGTH").map(Integer.valueOf).getOrElse(128)
    val USE_OPENVINO = sys.env.get("USE_OPENVINO").isDefined
    val CASE_SENSITIVE = sys.env.get("CASE_SENSITIVE").isDefined

    println(s" MODEL_PATH: $MODEL_PATH, " +
      s"DATA_PATH: $DATA_PATH, " +
      s"MODEL: $MODEL, " +
      s"BATCH_SIZE: ${BATCH_SIZE}, " +
      s"MAX_SENTENCE_LENGTH: ${MAX_SENTENCE_LENGTH}, " +
      s"CASE_SENSITIVE: ${CASE_SENSITIVE}, " +
      s"USE_OPENVINO: ${USE_OPENVINO} ")

    logger.info(s"Building spark session...")
    val spark = SparkSession
      .builder()
      .appName("Benchmark App")
      .master("local[*]")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .getOrCreate()

    var latency = 0d
    try {
      MODEL match {
        case "BERT" =>
          latency = benchmarkBertEmbeddings(MODEL_PATH, DATA_PATH, BATCH_SIZE, MAX_SENTENCE_LENGTH, spark, USE_OPENVINO, CASE_SENSITIVE)
          println(s"Latency: $latency ms")
        case "ROBERTA" =>
          latency = benchmarkRoBertaEmbeddings(MODEL_PATH, DATA_PATH, BATCH_SIZE, MAX_SENTENCE_LENGTH, spark, USE_OPENVINO, CASE_SENSITIVE)
          println(s"Latency: $latency ms")
        case "XLMROBERTA" =>
          latency = benchmarkXlmRoBertaEmbeddings(MODEL_PATH, DATA_PATH, BATCH_SIZE, MAX_SENTENCE_LENGTH, spark, USE_OPENVINO, CASE_SENSITIVE)
          println(s"Latency: $latency ms")
      }
    } finally {
      spark.close()
    }
  }

  private def benchmarkBertEmbeddings(
                                       modelPath: String, dataPath: String, batchSize: Integer,
                                       maxSentenceLength: Integer, spark: SparkSession, useOpenvino: Boolean,
                                       caseSensitive: Boolean): Double = {

    println("Loading dataset...")
    val conll = CoNLL(explodeSentences = false)
    val corpus = conll.readDataset(spark, dataPath)

    println(s"loading saved model from $modelPath, useOpenvino=$useOpenvino")
    val embeddings = BertEmbeddings
      .loadSavedModel(modelPath, spark, useOpenvino)
      .setInputCols("sentence", "token")
      .setOutputCol("embeddings")
      .setCaseSensitive(caseSensitive)
      .setMaxSentenceLength(maxSentenceLength)
      .setBatchSize(batchSize)

    val pipeline = new Pipeline()
      .setStages(Array(embeddings))

    measure(pipeline, corpus)
  }

  private def benchmarkRoBertaEmbeddings(
                                          modelPath: String, dataPath: String, batchSize: Integer,
                                          maxSentenceLength: Integer, spark: SparkSession, useOpenvino: Boolean,
                                          caseSensitive: Boolean): Double = {

    println("Loading dataset...")
    val conll = CoNLL(explodeSentences = false)
    val corpus = conll.readDataset(spark, dataPath)

    println(s"loading saved model from $modelPath, useOpenvino=$useOpenvino")
    val embeddings = RoBertaEmbeddings
      .loadSavedModel(modelPath, spark, useOpenvino)
      .setInputCols("sentence", "token")
      .setOutputCol("embeddings")
      .setCaseSensitive(caseSensitive)
      .setMaxSentenceLength(maxSentenceLength)
      .setBatchSize(batchSize)

    val pipeline = new Pipeline()
      .setStages(Array(embeddings))

    measure(pipeline, corpus)
  }

  private def benchmarkXlmRoBertaEmbeddings(
                                          modelPath: String, dataPath: String, batchSize: Integer,
                                          maxSentenceLength: Integer, spark: SparkSession, useOpenvino: Boolean,
                                          caseSensitive: Boolean): Double = {

    println("Loading dataset...")
    val conll = CoNLL(explodeSentences = false)
    val corpus = conll.readDataset(spark, dataPath)

    println(s"loading saved model from $modelPath, useOpenvino=$useOpenvino")
    val embeddings = XlmRoBertaEmbeddings
      .loadSavedModel(modelPath, spark, useOpenvino)
      .setInputCols("sentence", "token")
      .setOutputCol("embeddings")
      .setCaseSensitive(caseSensitive)
      .setMaxSentenceLength(maxSentenceLength)
      .setBatchSize(batchSize)

    val pipeline = new Pipeline()
      .setStages(Array(embeddings))

    measure(pipeline, corpus)
  }

  private def measure(pipeline: Pipeline, dataset: Dataset[_]): Double = {
    println("Running the pipeline..")
    val result = pipeline.fit(dataset).transform(dataset)
    val start = System.nanoTime()
    result.write.mode("overwrite").parquet("./tmp_bm")
    val dur = (System.nanoTime() - start) / 1e9

    println(s"Took $dur seconds...")
    dur
  }
}
