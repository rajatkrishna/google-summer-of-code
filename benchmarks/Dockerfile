FROM ubuntu:20.04 AS build_openvino

WORKDIR /opt/intel/repo

RUN apt-get update; \
    DEBIAN_FRONTEND=noninteractive \
    apt-get install -y --no-install-recommends \
        apt-utils \
        git \
        ca-certificates \
        curl \
        unzip \
        sudo \
        openjdk-8-jdk \
        tzdata; \
        rm -rf /var/lib/apt/lists/*; \
    curl -L https://services.gradle.org/distributions/gradle-7.4-bin.zip --output gradle-7.4-bin.zip; \
    unzip gradle-7.4-bin.zip -d /opt/gradle; \
    curl -L https://storage.openvinotoolkit.org/repositories/openvino/packages/2023.0.1/linux/l_openvino_toolkit_ubuntu20_2023.0.1.11005.fa1c41994f3_x86_64.tgz --output openvino.tgz; \
    sudo mkdir -p /opt/intel/openvino-2023.0.1; \
    tar -xvzf openvino.tgz -C /opt/intel/openvino-2023.0.1 --strip-components=1; \
    curl -L https://raw.githubusercontent.com/openvinotoolkit/openvino/master/install_build_dependencies.sh --output install_build_dependencies.sh; \
    chmod +x install_build_dependencies.sh; \
    ./install_build_dependencies.sh; \
    rm -rf openvino.tgz; \
    rm install_build_dependencies.sh

ENV OV_CONTRIB_FORK="rajatkrishna"
ENV OV_CONTRIB_BRANCH="sparknlp-integration"

SHELL ["/bin/bash", "-c"]

# Clone repositories
RUN git clone https://github.com/${OV_CONTRIB_FORK}/openvino_contrib.git -b ${OV_CONTRIB_BRANCH} --depth 1 /opt/intel/repo/openvino_contrib; \
    cd openvino_contrib/modules/java_api; \
    git clone https://github.com/openvinotoolkit/testdata -b releases/2023/0 --depth 1; \
    . /opt/intel/openvino-2023.0.1/setupvars.sh; \
    cmake /opt/intel/repo/openvino_contrib/modules/java_api -DCMAKE_BUILD_TYPE=Release; \
    make -j$(nproc --all); \
    sudo cp *.so /opt/intel/openvino-2023.0.1/runtime/lib/intel64; \
    /opt/gradle/gradle-7.4/bin/gradle clean build -Prun_tests -DMODELS_PATH=./testdata -Ddevice=CPU --info; \
    sudo mv /opt/intel/repo/openvino_contrib/modules/java_api/build/libs /opt/intel/openvino-2023.0.1/java

CMD ["/bin/bash"]
# -------------------------------------------------------------------------------------------------

FROM ubuntu:20.04 AS build_sparknlp

ENV INTEL_OPENVINO_DIR=/opt/intel/openvino-2023.0.1
COPY --from=build_openvino $INTEL_OPENVINO_DIR $INTEL_OPENVINO_DIR
    
WORKDIR /opt/intel/repo

# Install dependencies
RUN apt-get update; \
    DEBIAN_FRONTEND=noninteractive \
        apt-get install -yqq --no-install-recommends \
        apt-utils \
        git \
        ca-certificates \
        apt-transport-https \
        gnupg \
        curl \
        sudo \
        openjdk-8-jdk \
        tzdata; \
    rm -rf /var/lib/apt/lists/*; \
    chmod +x $INTEL_OPENVINO_DIR/install_dependencies/install_openvino_dependencies.sh; \
    $INTEL_OPENVINO_DIR/install_dependencies/install_openvino_dependencies.sh -y; \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list; \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list; \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo -H gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import; \
    chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg; \
    apt-get update; \
    apt-get install sbt; \
    curl -L https://archive.apache.org/dist/spark/spark-3.2.3/spark-3.2.3-bin-hadoop3.2.tgz --output spark.tgz; \
    mkdir /opt/spark-3.2.3; \
    sudo tar -xzf spark.tgz -C /opt/spark-3.2.3 --strip-components=1; \
    rm -rf spark.tgz; \
    export SPARK_HOME=/opt/spark-3.2.3; \
    export PATH=$PATH:$SPARK_HOME/bin

ARG SPARKNLP_FORK="rajatkrishna"
ARG SPARKNLP_BRANCH="bm-etst"

# Build Spark NLP jar
RUN git clone https://github.com/${SPARKNLP_FORK}/spark-nlp.git -b ${SPARKNLP_BRANCH} --depth 1; \
    cd spark-nlp; \
    mkdir -p lib; \
    cp $INTEL_OPENVINO_DIR/java/* ./lib; \
    sbt assemblyAndCopy -mem 4096; \
    mv ./python/lib/* /opt/spark-3.2.3/jars

CMD ["/bin/bash"]
# -------------------------------------------------------------------------------------------------

FROM ubuntu:20.04 AS spark-ov

RUN apt-get update; \
    DEBIAN_FRONTEND=noninteractive \
        apt-get install -y --no-install-recommends \
        apt-utils \
        git \
        ca-certificates \
        curl \
        sudo \
        tzdata \
        sudo \
        openjdk-8-jdk \
        unzip; \
    rm -rf /var/lib/apt/lists/*

ENV INTEL_OPENVINO_DIR=/opt/intel/openvino-2023.0.1
ENV SPARK_HOME=/opt/spark-3.2.3
COPY --from=build_sparknlp $INTEL_OPENVINO_DIR $INTEL_OPENVINO_DIR    
COPY --from=build_sparknlp /opt/spark-3.2.3 /opt/spark-3.2.3

RUN chmod +x ${INTEL_OPENVINO_DIR}/install_dependencies/install_openvino_dependencies.sh; \
    ${INTEL_OPENVINO_DIR}/install_dependencies/install_openvino_dependencies.sh -y; \
    . ${INTEL_OPENVINO_DIR}/setupvars.sh; \
    curl -fL https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-linux.gz | gzip -d > cs && chmod +x cs && ./cs install scala:2.12.15 scalac:2.12.15

ENV PATH=$PATH:$SPARK_HOME/bin:/root/.local/share/coursier/bin
RUN printf "\nsource \${INTEL_OPENVINO_DIR}/setupvars.sh\nexport SPARK_HOME=/opt/spark-3.2.3\nexport PATH=\${PATH}:\${SPARK_HOME}/bin:/root/.local/share/coursier/bin:/usr/local/lib" >> /root/.bashrc

WORKDIR /home
CMD ["/bin/bash"]