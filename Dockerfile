FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# jar 이름을 고정: app.jar
COPY app.jar /app/app.jar

ENV SERVER_PORT=8080
EXPOSE 8080

# alpine에서 java PATH 꼬이는 케이스 방지
ENTRYPOINT ["sh","-lc","/opt/java/openjdk/bin/java ${JAVA_OPTS:-} -jar /app/app.jar"]