# Imagen base optimizada de Java 21
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia el artefacto JAR generado por Maven
COPY target/*.jar app.jar

# Expone el puerto 8080 del backend
EXPOSE 8080

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
