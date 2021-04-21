FROM maven:3.6.3-jdk-11-slim AS MAVEN_BUILD
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn package -DskipTests
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/dystraite-0.0.1-SNAPSHOT.jar /app/
ENTRYPOINT ["java", "-jar", "dystraite-0.0.1-SNAPSHOT.jar"]