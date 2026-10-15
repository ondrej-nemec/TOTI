FROM codercom/code-server:latest

RUN mkdir -p /home/coder/project
WORKDIR /home/coder/project

USER root

# JAVA
RUN curl -sSL https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.5+11/OpenJDK21U-jdk_x64_linux_hotspot_21.0.5_11.tar.gz -o /tmp/openjdk21.tar.gz && \
    mkdir -p /opt/openjdk21 && \
    tar -xvzf /tmp/openjdk21.tar.gz -C /opt/openjdk21 --strip-components=1 && \
    rm -f /tmp/openjdk21.tar.gz && \
    update-alternatives --install /usr/bin/java java /opt/openjdk21/bin/java 1 && \
    update-alternatives --install /usr/bin/javac javac /opt/openjdk21/bin/javac 1

ENV JAVA_HOME=/opt/openjdk21
ENV PATH="$JAVA_HOME/bin:$PATH"

# TRIVY
RUN mkdir -p ~/.local/bin
RUN curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh | sh -s -- -b ~/.local/bin
ENV PATH="$HOME/.local/bin:$PATH"

# NPM
RUN apt-get update && apt-get install -y npm
RUN npm install jest
RUN npm install playwright
# for chromium
# RUN apt install libnspr4
# RUN apt install libnss3
# RUN apt install -y libatk1.0-0

USER coder

# EXPOSE 81 444 88

COPY start.sh /start.sh
USER root
RUN chmod 777 /start.sh
USER coder
ENTRYPOINT []
CMD ["bash", "/start.sh"]