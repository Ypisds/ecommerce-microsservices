# syntax=docker/dockerfile:1

# 1. ESTÁGIO DE BUILD (Compilação e empacotamento)
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /build

# Copia dependências do Maven Wrapper
COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/
COPY pom.xml .

# Baixa as dependências e compila o código usando cache local do Maven
COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests

# 2. ESTÁGIO FINAL / RUNTIME (Execução enxuta)
FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

# Cria usuário não-root por segurança
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser

# Copia apenas o .jar gerado no build
COPY --from=build --chown=appuser:appuser /build/target/*.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]