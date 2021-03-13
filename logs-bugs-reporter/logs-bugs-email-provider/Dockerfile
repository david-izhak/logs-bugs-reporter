FROM openjdk:15.0.2
EXPOSE 8686
ADD ./target/logs-bugs-email-provider-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]