# Spark-NLP Sample Applications

These sample Scala applications demonstrate how to load and run custom models in Spark NLP using OpenVINO Runtime. 
The following sample applications are included:

- [bert-embeddings](./bert-embeddings/): Generate BERT embeddings
- [bert-ner](./bert-ner/): Named Entity Recognition using BERT embeddings

## Docker

### Prerequisites

- Docker 24.0.2
    
    Follow the steps [here](https://docs.docker.com/engine/install/ubuntu/) to install Docker

### Steps

Use the [Dockerfile](../../../Dockerfile) provided to set up Spark NLP with the required dependencies. Build the Docker image using the following command from the project root:

```
docker build -t spark-nlp-ov .
```

Export the model to the `/models/bert` directory. Follow the instructions in [this notebook](../../../notebooks/Export_BERT_HuggingFace.ipynb) to export the `bert-base-cased` model from Hugging Face.

Then run the following commands to navigate to the sample directory, mount the model and app directories to the Docker container and run the Scala application. Replace \<input_string\> with the input string:

```
cd <sample_name>
docker run -v .:/app -v /models:/models spark-nlp-ov scala -classpath /opt/spark-3.2.3/jars/*:. /app/Main.scala -m file:/models/<saved_model> -i <input_string>
```

## Linux

### Prerequisites

- Scala 2.12.15

- OpenVINO 2023.0.1 with the Java bindings 

    Follow the instructions [here](../../openvino/build-ov-lin.md) to build OpenVINO with the Java bindings

- OpenJDK 8

- Apache Spark 3.2.3

- Spark NLP

    Follow the instructions [here](../../spark-nlp/spark-nlp-jar.md) to compile the Spark NLP jar from source

### Steps

Navigate to the sample directory and run the following command to launch the application:

```
scala -classpath <SPARK_DIR>/jars/*:<SPARKNLP_DIR>/python/lib/*:. Main.scala -m file:/<saved_model_dir> -i <input_string>
```