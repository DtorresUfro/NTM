# ---------- Etapa 1: Compilación ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar pom primero para aprovechar la caché
COPY pom.xml .

RUN mvn dependency:go-offline

# Copiar el código
COPY src ./src

# Compilar
RUN mvn clean package -DskipTests

# ---------- Etapa 2: Ejecución ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]