# ---------- Etapa 1: Build ----------
FROM gradle:8.5.0-jdk21 AS builder

# Crear y usar un directorio de trabajo
WORKDIR /home/gradle/src

# Copiamos solo los archivos de configuración primero para aprovechar la caché
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle
RUN gradle dependencies --no-daemon || true

# Copiamos el resto del código fuente
COPY . .

# Construimos el jar (sin correr tests para acelerar el build)
RUN gradle clean bootJar --no-daemon -x test

# ---------- Etapa 2: Runtime ----------
FROM eclipse-temurin:21-jdk-jammy AS runtime

# Crear directorio de trabajo en la imagen final
WORKDIR /app

# Copiar solo el jar generado desde la etapa anterior
COPY --from=builder /home/gradle/src/build/libs/*.jar app.jar

# Exponer el puerto de Spring Boot
EXPOSE 8080

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
