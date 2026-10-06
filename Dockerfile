FROM openjdk:JavaSE-17
COPY "./target/mundial-julieth-0.0.1-SNAPSHOT.jar" "app.jar"
EXPOSE "8112"
ENTRYPOINT [ "java" , "-jar", "app.jar"]
