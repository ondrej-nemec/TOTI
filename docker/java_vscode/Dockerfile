FROM codercom/code-server:latest

RUN mkdir -p /home/coder/project
WORKDIR /home/coder/project

# RUN code-server --install-extension bmewburn.vscode-intelephense-client

##############
# JAVA
USER root
RUN curl -sSL https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.5+11/OpenJDK21U-jdk_x64_linux_hotspot_21.0.5_11.tar.gz -o /tmp/openjdk21.tar.gz && \
    mkdir -p /opt/openjdk21 && \
    tar -xvzf /tmp/openjdk21.tar.gz -C /opt/openjdk21 --strip-components=1 && \
    rm -f /tmp/openjdk21.tar.gz && \
    update-alternatives --install /usr/bin/java java /opt/openjdk21/bin/java 1 && \
    update-alternatives --install /usr/bin/javac javac /opt/openjdk21/bin/javac 1

ENV JAVA_HOME=/opt/openjdk21
ENV PATH="$JAVA_HOME/bin:$PATH"
USER coder
# EXPOSE 81 444 88

COPY start.sh /start.sh
USER root
RUN chmod 777 /start.sh
USER coder
ENTRYPOINT []
CMD ["bash", "/start.sh"]