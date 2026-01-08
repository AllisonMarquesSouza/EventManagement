FROM eclipse-temurin:21 AS building

WORKDIR /app

ADD . /app

RUN ./mvnw package -DskipTests


FROM eclipse-temurin:21-alpine

WORKDIR /app

COPY --from=building /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]