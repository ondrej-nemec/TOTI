FROM codercom/code-server:latest

RUN mkdir -p /home/coder/project
WORKDIR /home/coder/project

RUN code-server --install-extension bmewburn.vscode-intelephense-client

##############
# JAVA
USER root
RUN curl -sSL https://github.com/adoptium/temurin18-binaries/releases/download/jdk-18.0.2.1+1/OpenJDK18U-jdk_x64_linux_hotspot_18.0.2.1_1.tar.gz -o /tmp/openjdk18.tar.gz && \
    mkdir -p /opt/openjdk18 && \
    tar -xvzf /tmp/openjdk18.tar.gz -C /opt/openjdk18 --strip-components=1 && \
    rm -f /tmp/openjdk18.tar.gz && \
    update-alternatives --install /usr/bin/java java /opt/openjdk18/bin/java 1 && \
    update-alternatives --install /usr/bin/javac javac /opt/openjdk18/bin/javac 1

ENV JAVA_HOME=/opt/openjdk18
ENV PATH="$JAVA_HOME/bin:$PATH"
USER coder
EXPOSE 81 444 88

# CMD code-server --bind-addr 0.0.0.0:88 --auth none
CMD ["code-server ", "--bind-addr", "0.0.0.0:88", "--auth", "none"]