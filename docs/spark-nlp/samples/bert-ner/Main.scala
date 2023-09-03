import com.johnsnowlabs.nlp.embeddings.BertEmbeddings
import com.johnsnowlabs.nlp.base.DocumentAssembler
import com.johnsnowlabs.nlp.annotator.SentenceDetector
import com.johnsnowlabs.nlp.annotators.Tokenizer
import com.johnsnowlabs.nlp.annotators.ner.dl.NerDLModel
import org.apache.spark.ml.Pipeline
import com.johnsnowlabs.nlp.EmbeddingsFinisher
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
        val sentence = new SentenceDetector().setInputCols("document").setOutputCol("sentence")
        val tokenizer = new Tokenizer().setInputCols(Array("sentence")).setOutputCol("token")
        val embeddings = BertEmbeddings
            .loadSavedModel(MODEL_PATH, spark, useOpenvino = true)
            .setInputCols("token", "document")
            .setOutputCol("embeddings")
            .setCaseSensitive(true)
            .setStorageRef("bert_base_cased")

        val nerModel = NerDLModel.pretrained("ner_dl_bert","en")
            .setInputCols("document","token","embeddings").setOutputCol("ner")
            
        val pipeline = new Pipeline().setStages(Array(document, sentence, tokenizer, embeddings, nerModel))

        val data = Seq(INPUT_STRING).toDF("text")

        val result = pipeline.fit(data).transform(data)
        result.select("ner.result").show(false)
    }
}