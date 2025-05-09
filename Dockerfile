FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . /app

RUN ./mvn clean package -DskipTests

CMD ["java", "-jar", "target/spa-app-0.0.1-SNAPSHOT.jar"]
