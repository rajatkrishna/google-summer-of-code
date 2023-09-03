import com.johnsnowlabs.nlp.embeddings.BertEmbeddings
import com.johnsnowlabs.nlp.base.DocumentAssembler
import com.johnsnowlabs.nlp.annotators.Tokenizer
import org.apache.spark.ml.Pipeline
import com.johnsnowlabs.nlp.EmbeddingsFinisher
import org.apache.spark.sql.functions.{explode, col}
import org.apache.spark.sql.SparkSession

object Main {

    def main(args: Array[String]): Unit = {

        if (!args.contains("-m")) {
            throw new RuntimeException("Missing model path!")
        }
        if (!args.contains("-i")) {
            throw new RuntimeException("Missing input string!")
        }
        val MODEL_PATH = args(args.indexOf("-m") + 1)
        val INPUT_STRING = args(args.indexOf("-i") + 1)

        val spark = SparkSession
            .builder()
            .appName("Benchmark App")
            .master("local[*]")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .config("spark.driver.memory", "4G")
            .getOrCreate()

        import spark.implicits._

        val document = new DocumentAssembler().setInputCol("text").setOutputCol("document")
        val tokenizer = new Tokenizer().setInputCols(Array("document")).setOutputCol("token")
        val embeddings = BertEmbeddings
            .loadSavedModel(MODEL_PATH, spark, useOpenvino = true)
            .setInputCols("token", "document")
            .setOutputCol("embeddings")
            .setCaseSensitive(true)
            .setStorageRef("bert_base_cased")

        val embeddingsFinisher = new EmbeddingsFinisher()
            .setInputCols("embeddings")
            .setOutputCols("finished_embeddings")
            
        val pipeline = new Pipeline().setStages(Array(document, tokenizer, embeddings, embeddingsFinisher))

        val data = Seq(INPUT_STRING).toDF("text")

        val result = pipeline.fit(data).transform(data)

        result.select(explode($"finished_embeddings") as "result").show(5, 100)
    }
}