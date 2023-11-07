# Spark NLP (Linux) Build Instructions

[Spark NLP](https://github.com/JohnSnowLabs/spark-nlp) is an open-source NLP library built on top of Apache Spark, offering production-ready NLP annotations for Machine Learning pipelines and state-of-the-art transformers like BERT, RoBERTa, etc. Written in Scala, Spark NLP runs on top of the Java Virtual Machine and can be integrated with your Java, Scala and Kotlin applications in addition to the Python API it offers.

This demo describes the steps to compile and build the jar for using Spark NLP in your Spark applications.

## Prerequisites

- OpenJDK 8

    To install using `apt`, run the following command
    ```
    apt update
    apt install openjdk-8-jdk
    ```
    
- sbt 1.9.3
    
    To install sbt version 1.9.3 using [SDKMAN](https://sdkman.io/install)

    ```
    sdk install sbt 1.9.3
    ```

- OpenVINO 2023.0.1 with Java bindings

    Follow the steps [here](../openvino/build-ov-lin.md) to setup OpenVINO and compile the jar

## Build

- Clone the source repository from GitHub. Use the feature branch `feature/ov-integration` with the latest changes to use OpenVINO Runtime.

    ```
    git clone https://github.com/rajatkrishna/spark-nlp.git -b feature/ov-integration
    cd spark-nlp
    ```

- Create a new directory `lib` in the project root and copy the OpenVINO jar to this location.

    ```
    mkdir lib
    cp <openvino.jar> lib/ 
    ```

- Set the following environment variable to increase the heap space

    ```
    export SBT_OPTS="-Xmx4G"
    ```

- Then run the following command to compile the project and all its dependencies into a fat jar at `python/lib/`

    ```
    sbt assemblyAndCopy
    ```

To load the Spark NLP jar and open a Spark shell, use the following command:

```
spark-shell --jars /path/to/spark-nlp.jar
```