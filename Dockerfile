FROM gradle:8.10.1-jdk17 AS builder
WORKDIR /app

#Copy wrapper + metadata 
COPY gradlew ./
COPY gradle/wrapper ./gradle/wrapper
COPY build.gradle settings.gradle gradle.properties* ./

#Prefetch deps (cache)
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies || true

#Copy sources and build
COPY src ./src
RUN ./gradlew clean bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
ENV SERVER_PORT=8088
COPY --from=builder /app/build/libs/*.jar /app/app.jar
EXPOSE 8088
ENTRYPOINT ["java","-Dserver.port=${SERVER_PORT}","-jar","/app/app.jar"]
