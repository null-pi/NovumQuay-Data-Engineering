FROM eclipse-temurin:21.0.5_11-jdk-noble AS build

WORKDIR /app

COPY mvnw ./
COPY .mvn ./.mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

COPY src ./src
# Skip tests during the build process to speed it up and avoid environment-specific failures
RUN ./mvnw clean package -DskipTests && cp target/*.jar app.jar

############################################

FROM eclipse-temurin:21-jre-noble AS runtime

WORKDIR /app

USER root

# Updated RUN command to install noVNC and websockify
RUN apt-get update && \
    apt-get install -y --no-install-recommends wget gnupg ca-certificates && \
    wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-chrome-keyring.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y --no-install-recommends \
    google-chrome-stable \
    tigervnc-standalone-server \
    tigervnc-common \
    fluxbox \
    dbus-x11 \
    xterm \
    novnc \
    websockify && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/app.jar .
COPY start.sh .
RUN chmod +x start.sh

ENTRYPOINT ["./start.sh"]
