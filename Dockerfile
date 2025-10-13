# Build
FROM gradle:8.10.1-jdk17 AS builder
WORKDIR /app
COPY build.gradle settings.gradle gradle gradlew ./
COPY src ./src
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test --no-daemon

# Run
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
ENV SERVER_PORT=8088
COPY --from=builder /app/build/libs/*.jar /app/app.jar
EXPOSE 8088
ENTRYPOINT ["java","-Dserver.port=${SERVER_PORT}","-jar","/app/app.jar"]
