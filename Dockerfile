FROM eclipse-temurin:25-jdk-noble AS build
WORKDIR /src
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY gradle/ gradle/
COPY src/ src/
RUN sh ./gradlew --no-daemon bootJar && cp build/libs/*.jar /madapay.jar

FROM eclipse-temurin:25-jre-noble
WORKDIR /app
COPY --from=build /madapay.jar ./madapay.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=Indian/Antananarivo", "-jar", "/app/madapay.jar"]
