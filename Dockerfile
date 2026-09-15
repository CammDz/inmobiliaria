# Paso 1: Compilar la aplicación con Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar el código del proyecto y compilar
COPY . .
RUN mvn clean package -DskipTests

# Paso 2: Ejecutar en Apache Tomcat
FROM tomcat:10.1-jdk17-temurin
WORKDIR /usr/local/tomcat/webapps/

# Eliminar la aplicación por defecto de Tomcat y copiar el archivo WAR compilado
RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]