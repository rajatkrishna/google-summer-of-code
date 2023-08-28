# Google Summer of Code

This repository serves as a final report detailing the work done as part of Google Summer of Code 2023 for integrating the OpenVINO Runtime API in Spark NLP. 

## Project Abstract

Performance-focused, production-level machine learning libraries need to leverage the resources at their disposal to the maximum extent to deliver efficient and effective machine learning workflows that ultimately result in improved user experience. SparkNLP, one such library widely adopted and used by 16% of enterprise companies(as of Feb 2019), is currently capable of taking advantage of CPU optimization capabilities using Intel-optimized Tensorflow. This coupled with other optimizations already allows it to run machine learning pipelines orders of magnitude faster than legacy libraries. Such a library would benefit from solutions like OpenVINO that offer extensive integrations in the ML ecosystem and even further optimization capabilities for inferring and deploying models on a range of hardware platforms. Exposing the OpenVINO API bindings in Java will allow integration with SparkNLP to enable the above-mentioned capabilities, and furthermore, open up avenues for a large community of developers to benefit from OpenVINO’s rich feature set in the future.

## Deliverables

- Add required JNI Bindings to the OpenVINO Java module 
- Using the OpenVINO Runtime API to import and run models in Spark NLP
- [Benchmarking](./benchmarks/README.md) models run with the new OpenVINO backend
- [Sample scripts](./docs/spark-nlp/samples/) demonstrating the usage of this feature 
- [Sample notebooks](./notebooks/) demonstrating how to export and prepare models 
## Contributions

| PR Link   | Description  | 
| :-----------: | :------------------------------------:|
| [PR #709](https://github.com/openvinotoolkit/openvino_contrib/pull/709) | [JAVA_API][GSOC] Add Java API bindings |
| [PR #668](https://github.com/openvinotoolkit/openvino_contrib/pull/668) | [JAVA_API] Reorganize project structure and automatically load native libraries | 
| [PR #13947](https://github.com/JohnSnowLabs/spark-nlp/pull/13947) | Integrating OpenVINO Runtime in Spark NLP | 

