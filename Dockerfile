FROM gradle:8.11.1-jdk21 AS builder
WORKDIR /app

# Copy gradle wrapper and build files first for better layer caching
COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY gradlew gradlew

# Copy source
COPY src src

# Build the application (skip tests for image build speed; run tests separately in CI)
RUN chmod +x ./gradlew && ./gradlew bootJar -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ENV JAVA_OPTS="" \
    SERVER_PORT=8080

COPY --from=builder /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD wget -qO- http://127.0.0.1:${SERVER_PORT}/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
