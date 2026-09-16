FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

EXPOSE 7000

CMD ["mvn", "exec:java"]
