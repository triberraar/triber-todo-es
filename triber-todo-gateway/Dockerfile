FROM openjdk:8-jdk-alpine

ARG tag

COPY ./target/triber-todo-gateway-$tag.jar app.jar
RUN sh -c 'touch /app.jar'

EXPOSE 8000

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]