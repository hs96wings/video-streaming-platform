FROM gradle:7.6-jdk17 AS builder
WORKDIR /project
COPY gradlew build.gradle settings.gradle ./
COPY gradle gradle

RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /project/build/libs/*.jar app.jar

RUN mkdir -p /app/uploads/videos && chown -R 1000:1000 /app/uploads

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]