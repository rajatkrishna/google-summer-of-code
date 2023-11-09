# Google Summer of Code

This repository serves as a final report summarizing my contributions during Google Summer of Code 2023 with [OpenVINO](https://github.com/openvinotoolkit/openvino). The project involved exposing the [OpenVINO Runtime API](https://docs.openvino.ai/2023.1/openvino_docs_OV_UG_OV_Runtime_User_Guide.html) in Java using the JNI framework and adding support for OpenVINO Runtime in [John Snow Labs Spark NLP](https://github.com/JohnSnowLabs/spark-nlp/tree/master), a high-performance NLP library. 

## Project Abstract

Spark NLP is an open-source, NLP library widely used in production that offers simple, performant and accurate NLP annotations for machine learning pipelines that can scale easily in distributed environments. It provides an enterprise-grade, unified solution that comes with thousands of pretrained models, pipelines and several NLP features that enable users to build end-to-end NLP pipelines and fits seamlessly into your data processing pipeline by extending Apache Spark natively. Written in Scala, Spark NLP provides support for Python, R and the JVM ecosystem (Java, Scala and Kotlin). Currently, it offers CPU optimization capabilities via Intel-optimized Tensorflow and ONNX Runtime, and supports importing custom models in the Tensorflow SavedModel and ONNX formats.

This project aims to enhance the capabilities of Spark NLP by adding support for OpenVINO Runtime, providing significant out-of-the-box improvements for LLM models including BERT, especially on CPU and integrated GPU-based systems, and extending support for various model formats like ONNX, PaddlePaddle, TensorFlow, TensorFlow Lite and OpenVINO IR. Combined with further optimization and quantization capabilities offered by the OpenVINO Toolkit ecosystem when exporting models, OpenVINO Runtime will serve as a unified, high-performance inference engine capable of delivering accelerated inferencing of NLP pipelines on a variety of Intel hardware platforms. Furthermore, exposing the OpenVINO API bindings in Java will open up avenues for a large community of Java developers to benefit from OpenVINO's rich feature set as an inference and deployment solution for JVM-based projects in the future.

## Key Deliverables

- Add required JNI Bindings to the OpenVINO Java module 
- Enable the OpenVINO Runtime API to import and run models in Spark NLP
- Benchmark models run with the new OpenVINO backend
- Sample scripts demonstrating the usage of this feature 
- Sample notebooks demonstrating how to export and prepare models 

## Contributions

| PR Link   | Description  | 
| :--------: | :---------:|
| [PR #668](https://github.com/openvinotoolkit/openvino_contrib/pull/668) | Reorganize project structure and improve documentation | 
| [PR #709](https://github.com/openvinotoolkit/openvino_contrib/pull/709) | Add Java API bindings |
| [PR #13947](https://github.com/JohnSnowLabs/spark-nlp/pull/13947) | Integrating OpenVINO Runtime in Spark NLP | 

## Models Covered

| Spark NLP | Notebook  | Sample |
| :-- | :-- | :-- |
| **BertEmbeddings** | [Export BERT HuggingFace](https://github.com/rajatkrishna/google-summer-of-code/blob/main/notebooks/Export_BERT_HuggingFace.ipynb) | <ul><li>[BERT Embeddings](https://github.com/rajatkrishna/google-summer-of-code/blob/main/docs/spark-nlp/samples/bert-embeddings/sparknlp-bert-embeddings-ov.md)</li><li>[Named Entity Recognition with BERT Embeddings](https://github.com/rajatkrishna/google-summer-of-code/blob/main/docs/spark-nlp/samples/bert-ner/spark-nlp-bert-ov-ner.md)</li></ul> |
| **RoBertaEmbeddings** | [Export RoBerta HuggingFace](https://github.com/rajatkrishna/google-summer-of-code/blob/main/notebooks/Export_RoBERTa_HuggingFace.ipynb) |
| **XlmRoBertaEmbeddings** | [Export XLM RoBerta HuggingFace](https://github.com/rajatkrishna/google-summer-of-code/blob/main/notebooks/Export_XLM_RoBERTa_HuggingFace.ipynb) |

## Blogs and other resources

- [The Need for Speed: Accelerating NLP Inferencing in Spark NLP with OpenVINO™ Runtime](https://medium.com/openvino-toolkit/the-need-for-speed-accelerating-nlp-inferencing-in-spark-nlp-with-openvino-runtime-327638fcec80)
- [Deep Learning Inference in Java with OpenVINO™ Runtime](https://medium.com/openvino-toolkit/deep-learning-inference-in-java-with-openvino-runtime-2ed8fe0b4897)
- [Spark NLP-OpenVINO Integration Architecture](https://github.com/rajatkrishna/google-summer-of-code/blob/main/docs/spark-nlp/sparknlp-openvino.md)
- [OpenVINO Java Setup (Linux)](https://github.com/rajatkrishna/google-summer-of-code/blob/main/docs/openvino/build-ov-lin.md)
- [OpenVINO Java Setup (Windows)](https://github.com/rajatkrishna/google-summer-of-code/blob/main/docs/openvino/build-ov-win.md)
- [Spark NLP Dev Setup](https://github.com/rajatkrishna/google-summer-of-code/blob/main/docs/spark-nlp/spark-nlp-dev.md)
- [Build Spark NLP Jar](https://github.com/rajatkrishna/google-summer-of-code/blob/main/docs/spark-nlp/spark-nlp-jar.md)
- [Spark NLP-OpenVINO Dockerfile](https://github.com/rajatkrishna/google-summer-of-code/blob/main/Dockerfile)
- [Benchmarks](https://github.com/rajatkrishna/google-summer-of-code/blob/main/benchmarks/README.md)
