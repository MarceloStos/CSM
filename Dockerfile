# Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copiar os arqivos do Maven e o codigo fonte
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Dá permissão de execução ao mvnw e compila o projeto
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia apenas o arquivo .jar gerado no Build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta que o Spring Boot usa
EXPOSE 8080

# Comando para rodar a aplicacao
ENTRYPOINT ["java", "-jar", "app.jar"]