FROM eclipse-temurin:17-jdk
COPY "./target/mundial-julieth-0.0.1-SNAPSHOT.jar" "app.jar"
EXPOSE 8112
ENTRYPOINT [ "java", "-jar", "app.jar"]
