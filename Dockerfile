FROM eclipse-temurin:21.0.5_11-jdk-noble AS build

WORKDIR /app

COPY mvnw ./

COPY .mvn ./.mvn

COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package && cp target/*.jar app.jar

############################################

FROM eclipse-temurin:21-jre-noble AS runtime

WORKDIR /app

USER root

ARG VNC_PASSWORD="vncpassword"

ENV VNC_PASSWORD=${VNC_PASSWORD} \
    DISPLAY=:1 \
    VNC_PORT=5901 \
    CHROME_BIN=/usr/bin/google-chrome \
    CHROME_PATH=/usr/bin/google-chrome

ENV VNC_RESOLUTION=1920x1080 \
    VNC_COL_DEPTH=24 \
    VNC_GEOMETRY=1920x1080

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    wget \
    gnupg \
    # Install Google Chrome Stable
    && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update && \
    apt-get install -y --no-install-recommends \
    google-chrome-stable \
    # Install TigerVNC server and Fluxbox window manager
    tigervnc-standalone-server \
    tigervnc-common \
    fluxbox \
    # dbus-x11 is useful for some desktop applications like Chrome
    dbus-x11 \
    # xterm for a basic terminal, net-tools for debugging (optional)
    xterm \
    net-tools \
    # Necessary fonts and libraries
    fonts-liberation \
    libu2f-udev \
    # Clean up
    && apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/app.jar .