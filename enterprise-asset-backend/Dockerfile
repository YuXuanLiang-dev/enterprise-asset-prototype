# 使用 Guide 要求的 JDK 21
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# 跳过测试以加速构建
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
ENV LANG C.UTF-8
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Dsun.jnu.encoding=UTF-8", "-jar", "app.jar"]
