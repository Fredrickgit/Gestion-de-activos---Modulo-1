# syntax=docker/dockerfile:1

# ---------------------------------------------------------------------------
# Etapa 1: compilación del JAR con Maven y JDK 21
# ---------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

# Se copia primero el pom para aprovechar la caché de capas de Render:
# si no cambian las dependencias, no se vuelven a descargar.
COPY pom.xml ./
RUN mvn -B -q -Dmaven.test.skip=true dependency:go-offline

COPY src ./src
RUN mvn -B -q -Dmaven.test.skip=true package

# ---------------------------------------------------------------------------
# Etapa 2: imagen final, solo JRE 21 (más liviana y sin herramientas de build)
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-jammy AS runtime

# Plan gratuito de Render: 512 MB de RAM. Se limita la heap al 75 % del
# contenedor para dejar espacio a metaspace, hilos y memoria nativa.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=40 -XX:+UseSerialGC"

WORKDIR /app

# La aplicación no necesita ejecutarse como root.
RUN groupadd --system --gid 1001 spring \
    && useradd --system --uid 1001 --gid spring --home-dir /app --shell /usr/sbin/nologin spring

COPY --from=build /workspace/target/*.jar /app/app.jar
RUN chown -R spring:spring /app

USER spring:spring

# Render asigna el puerto mediante la variable PORT (por defecto 10000).
# application.yaml ya lee server.port=${PORT:8080}.
EXPOSE 8080

# exec reemplaza el shell por la JVM para que Render pueda enviar SIGTERM
# correctamente durante los despliegues y apagados.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
