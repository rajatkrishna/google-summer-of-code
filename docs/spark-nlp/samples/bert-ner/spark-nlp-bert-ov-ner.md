# Named Entity Recognition using Bert Embeddings with Spark NLP + OpenVINO™ (Linux)

Named Entity Recognition (NER) is an important NLP subtask that seeks to locate and classify relevant entites in unstructured input text into pre-defined categories like person, location and organization. This has several real-world applications including scanning entire news articles to identify entities of interest like places and people.

Spark NLP allows you to build and use state-of-the-art NER pipelines easily with just a few lines of code. This example demonstrates how to import a `BERT` model from HuggingFace into Spark NLP, and use the `BertEmbeddings` annotator to generate word embeddings. These embeddings are then used to identify entities using a pretrained `NerDLModel`.

## Prerequisites

1. OpenJDK 8 

    ```
    sudo apt update
    sudo apt install openjdk-8-jdk
    ```

2. OpenVINO 2023.0 or higher

    Follow the instructions [here](../../openvino/build-ov-lin.md) to build OpenVINO with the Java bindings from source. Install the OpenVINO components to `/opt/intel/openvino-2023.0`.

    Use the provided script to install the necessary dependencies.

    ```
    sudo -E /opt/intel/openvino-2023.0.1/install_dependencies/install_openvino_dependencies.sh
    ```

    Then run the `setupvars` script to set up the OpenVINO environment variables and add the libraries to PATH.

    ```
    source /opt/intel/openvino-2023.0.1/setupvars.sh
    ```

3. Spark 3.2.3 or higher

    Download Apache Spark from the official release archives.

    ```
    curl -L https://archive.apache.org/dist/spark/spark-3.2.3/spark-3.2.3-bin-hadoop3.2.tgz --output spark-3.2.3-bin-hadoop3.2.tgz
    ```

    Unpack the archive to the desired install location. The following command extracts the archive to `/opt/spark-3.2.3`

    ```
    mkdir /opt/spark-3.2.3
    sudo tar -xzf spark-3.2.3-bin-hadoop3.2.tgz -C /opt/spark-3.2.3 --strip-components=1
    ```

    Set up Apache Spark environment variables and add the binaries to PATH by adding the following lines to the `.bashrc` file

    ```
    vi ~/.bashrc

    # Add the following lines at the end of the .bashrc file
    export SPARK_HOME=/opt/spark-3.2.3
    export PATH=$PATH:$SPARK_HOME/bin
    ```

    Finally, load these environment variables to the current terminal session by running the following command

    ```
    source ~/.bashrc
    ```

    Verify installation by running

    ```
    spark-submit --version
    ```

4. Spark NLP 

    Follow the steps [here](../spark-nlp-jar.md) to compile the jar from source. 


## Model

This [notebook](../../../notebooks/Export_BERT_HuggingFace.ipynb) demonstrates how to export a BERT model from HuggingFace. In this example, we import this model into Spark NLP using the OpenVINO Runtime backend. 

Move the exported saved model directory to a new `models` folder as follows so we can locate it later

```
mkdir /root/models
mv <model_name>/saved_model/1 /root/models/bert-base-cased
```


## Spark Shell

Spark provides an interactive shell which offers a convenient way to quickly test out Spark statements and learn the API. Run the following command to launch the Spark Scala REPL

```
spark-shell --jars ~/spark-nlp/python/lib/sparknlp.jar --driver-memory=4g
```

The SparkSession class is the entrypoint into all Spark functionality. The active Spark Session will be available in the shell environment as `spark`.

Now we can start writing the pipeline. First import the required classes.

```
import com.johnsnowlabs.nlp.embeddings.BertEmbeddings
import com.johnsnowlabs.nlp.base.DocumentAssembler
import com.johnsnowlabs.nlp.annotator.SentenceDetector
import com.johnsnowlabs.nlp.annotators.Tokenizer
import com.johnsnowlabs.nlp.annotators.ner.dl.NerDLModel
import org.apache.spark.ml.Pipeline
import com.johnsnowlabs.nlp.EmbeddingsFinisher
import org.apache.spark.sql.functions.explode
```

Set the model path to the model to import

```
val MODEL_PATH="file:/root/models/bert-base-cased"
```

The `DocumentAssembler` converts the raw text data into the **Document** type for further processing in Spark

```
val document = new DocumentAssembler().setInputCol("text").setOutputCol("document")
```

Then extract the sentences in the input text for the NerDL model

```
val sentence = new SentenceDetector().setInputCols("document").setOutputCol("sentence")
```

Next, we need to split the input sentences into tokens.

```
val tokenizer = new Tokenizer().setInputCols(Array("sentence")).setOutputCol("token")
```

The `BertEmbeddings` class provides a `loadSavedModel` function to import external BERT models into Spark NLP. Set the `useOpenvino` flag to load the model using OpenVINO Runtime.

Set the `storageRef` property to the string `bert_base_cased`. For downstream tasks like NER or any Text Classification, Spark NLP uses this reference to bound the trained model to this specific embeddings.

```
val embeddings = BertEmbeddings.loadSavedModel(MODEL_PATH, spark, useOpenvino = true).setInputCols("token", "document").setOutputCol("embeddings").setCaseSensitive(true).setStorageRef("bert_base_cased")
```

Next, we use the pre-trained NerDLModel trained on the CoNLL 2003 dataset to find features using the embeddings produced by BERT.

```
val nerModel = NerDLModel.pretrained("ner_dl_bert","en").setInputCols("document","token","embeddings").setOutputCol("ner")
```

Finally, define the pipeline, transform a sample sentence and display the NER output.

```
val pipeline = new Pipeline().setStages(Array(document, sentence, tokenizer, embeddings, nerModel))

val data = Seq("Joel lives in Seattle with his wife Linda").toDF("text")

val result = pipeline.fit(data).transform(data)
result.select("ner.result").show(false)
```

![Alt text](../img/spark-nlp-ner.png)