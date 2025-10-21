# Multi-stage build para aplicação Java Spring Boot

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .

# Baixar dependências (cache layer)
RUN mvn dependency:go-offline

# Copiar código fonte
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Criar usuário não-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR do stage de build
COPY --from=build /app/target/desafio-ia-monolito-1.0-SNAPSHOT.jar app.jar

# Expor porta
EXPOSE 5000

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:5000/ || exit 1

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.profiles.active=production"]

