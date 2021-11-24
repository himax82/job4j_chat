FROM openjdk:15
WORKDIR chat
ADD auth/target/auth-1.0.jar app.jar
ENTRYPOINT java -jar app.jar